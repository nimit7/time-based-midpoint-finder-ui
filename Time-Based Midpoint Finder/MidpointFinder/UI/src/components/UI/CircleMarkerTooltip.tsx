import React from "react";
import { CircleMarker, CircleMarkerProps, Tooltip, TooltipProps } from "react-leaflet";


type CircleMarkerTooltipProps = Omit<CircleMarkerProps, "center" | "radius" > & {
  coordinates: [number, number];
  radius?: number;                
  color?: string;       
  className?: string;             // Override tooltip class
  offset?: [number, number];      //Override tooltip offset
  tooltipProps?: TooltipProps; 
  children?: React.ReactNode;
};

const CircleMarkerTooltip: React.FC<CircleMarkerTooltipProps> = ({
  coordinates,
  radius = 4,
  color = "green",
  className="bg-white p-2 rounded shadow-lg",
  offset=[10, 10],
  tooltipProps,
  children,
}) => {
  return (
    <CircleMarker center={coordinates} radius={radius} color={color}>
      <Tooltip
        permanent
        className={className}
        offset={offset}
        {...tooltipProps}
      >
        {children}
      </Tooltip>
    </CircleMarker>
  );
};

export default CircleMarkerTooltip;
