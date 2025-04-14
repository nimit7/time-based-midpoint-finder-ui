import React from "react";
import "./MyButton.scss";

interface MyButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  label?: string;
  onClick?: () => void;
  disabled?: boolean;
  children?: React.ReactNode;
}

const MyButton: React.FC<MyButtonProps> = ({
  label,
  onClick,
  children,
  className,
  ...props
}) => {
  return (
    <button
      onClick={onClick}
      className={`my-button ${className ?? ""}`}
      {...props}
    >
      {label}
      {children}
    </button>
  );
};

export default MyButton;
