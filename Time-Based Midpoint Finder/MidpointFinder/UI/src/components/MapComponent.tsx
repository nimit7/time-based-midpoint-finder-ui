import axios from "axios";
import React, { useState } from "react";
import {
  CircleMarker,
  MapContainer,
  Polyline,
  TileLayer,
  Tooltip,
  useMapEvents,
} from "react-leaflet";
import CircleMarkerTooltip from "./UI/CircleMarkerTooltip";
import MyButton from "./UI/Button/MyButton";

// Helper functions

const formatDistance = (meters: number): string => {
  if (meters < 1000) {
    return `${meters.toFixed(0)} m`;
  }
  if (meters < 10000) {
    return `${(meters / 1000).toFixed(2)} km`;
  }
  return `${(meters / 1000).toFixed(1)} km`;
};

const formatDuration = (seconds: number): string => {
  if (seconds < 60) {
    return `${seconds.toFixed(0)} sec`;
  }
  if (seconds < 3600) {
    return `${(seconds / 60).toFixed(0)} min`;
  }
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  return minutes > 0 ? `${hours}h ${minutes}m` : `${hours}h`;
};

type Path = {
  distance: number;
  duration: number;
  wayPoints: [number, number][];
};

const MapComponent: React.FC = () => {
  const center: [number, number] = [28, 77];

  const [startPoint1, setStartPoint1] = useState<[number, number] | null>(null);
  const [startPoint2, setStartPoint2] = useState<[number, number] | null>(null);
  const [pendingStartPoint, setPendingStartPoint] = useState<
    [number, number] | null
  >(null);
  const [waypoints, setWaypoints] = useState<[number, number][]>([]);
  const [midPoint, setMidPoint] = useState<[number, number] | null>(null);
  const [paths, setPaths] = useState<Path[]>([]);

  const handleMapClick = (e: any) => {
    if (!startPoint1) {
      setStartPoint1([e?.latlng?.lat, e?.latlng?.lng]);
    } else if (!startPoint2) {
      setStartPoint2([e?.latlng?.lat, e?.latlng?.lng]);
    } else {
      setPendingStartPoint([e?.latlng?.lat, e?.latlng?.lng]);
    }
  };

  // API
  const getWaypoints = async () => {
    if (!startPoint1 || !startPoint2) {
      alert("Please select two points on the map.");
      return;
    }

    try {
      const response = await axios.post(
        "http://localhost:8080/api/routes/waypoints",
        [getLatLngObj(startPoint1), getLatLngObj(startPoint2)]
      );
      setWaypoints(response.data); // Expecting an array of waypoints [[lat1, lng1], [lat2, lng2], ...]
    } catch (error) {
      console.error("Error fetching waypoints:", error);
    }
  };

  const getMidpoint = () => {
    if (!startPoint1 || !startPoint2) {
      alert("Please select two points on the map.");
      return;
    }

    axios
      .post("http://localhost:8080/api/routes/midpoint", [
        getLatLngObj(startPoint1),
        getLatLngObj(startPoint2),
      ])
      .then((response) => {
        setMidPoint(response.data); // Assuming response.data is [lat, lng]
        setPaths([]);
        return Promise.all([
          getPath(startPoint1, response.data),
          getPath(startPoint2, response.data),
        ]);
      })
      .catch((error) => {
        console.error("Error fetching midpoint:", error);
      });
  };

  const getPath = (
    coord1: [number, number] | null,
    coord2: [number, number] | null
  ) => {
    if (!coord1 || !coord2) {
      alert("Please select two points on the map.");
      return;
    }

    return axios
      .post("http://localhost:8080/api/routes/path", [
        getLatLngObj(coord1),
        getLatLngObj(coord2),
      ])
      .then((response) => {
        setPaths((prev) => [...prev, response.data]);
        // setPath(response.data); // Assuming response.data is the path
        return response.data;
      })
      .catch((error) => {
        console.error("Error fetching midpoint:", error);
      });
  };

  const setNewStartPoint = (startPoint: string) => {
    if (startPoint === "1") setStartPoint1(pendingStartPoint);
    else if (startPoint === "2") setStartPoint2(pendingStartPoint);
    setPendingStartPoint(null);
    setMidPoint(null);
    setPaths([]);
  };

  const MapClickHandler = () => {
    useMapEvents({ click: handleMapClick });
    return null;
  };

  const resetMap = () => {
    setStartPoint1(null);
    setStartPoint2(null);
    setPendingStartPoint(null);
    setWaypoints([]);
    setMidPoint(null);
    setPaths([]);
  };

  return (
    <div>
      <MapContainer
        center={center}
        zoom={13}
        style={{ width: "100%", height: "500px" }}
      >
        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
        <MapClickHandler />
        {startPoint1 && (
          <CircleMarkerTooltip coordinates={startPoint1}>
            <div className="text-sm font-medium">Start 1</div>
          </CircleMarkerTooltip>
        )}
        {startPoint2 && (
          <CircleMarkerTooltip coordinates={startPoint2}>
            <div className="text-sm font-medium">Start 2</div>
          </CircleMarkerTooltip>
        )}
        {pendingStartPoint && (
          <CircleMarkerTooltip
            coordinates={pendingStartPoint}
            color="orange"
            tooltipProps={{ interactive: true }}
          >
            <div
              className="text-sm font-medium"
              onClick={(e) => e.stopPropagation()}
            >
              <MyButton onClick={() => setNewStartPoint("1")}>Set Start 1</MyButton>
              <MyButton onClick={() => setNewStartPoint("2")}>Set Start 2</MyButton>
            </div>
          </CircleMarkerTooltip>
        )}

        {paths.map((path, i) => {
          return (
            <Polyline
              key={`path-${i}`}
              positions={path?.wayPoints.map((wp: any[]) => [wp[1], wp[0]])}
              color="blue"
            >
              <Tooltip
                permanent
                // direction="auto"
                className="bg-white p-2 rounded shadow-lg"
                offset={[10, 10]}
              >
                <div className="text-sm font-medium">
                  <div>{formatDistance(path?.distance)}</div>
                  <div>{formatDuration(path?.duration)}</div>
                </div>
              </Tooltip>
            </Polyline>
          );
        })}

        {/* Midpoint Marker */}
        {midPoint && <CircleMarker center={midPoint} radius={4} color="red" />}
      </MapContainer>

      <MyButton onClick={() => getPath(startPoint1, startPoint2)}>
        Get Path
      </MyButton>
      <MyButton onClick={getMidpoint} disabled={!paths?.length}>
        Get Midpoint
      </MyButton>
      <MyButton
        onClick={resetMap}
        className="bg-red-500 hover:bg-red-600 text-white"
      >
        Reset
      </MyButton>
    </div>
  );
};

const getLatLngObj = (arr: [number, number]) => {
  return {
    latitude: arr[0],
    longitude: arr[1],
  };
};

export default MapComponent;
