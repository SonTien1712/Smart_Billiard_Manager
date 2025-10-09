import { Toaster as Sonner } from "sonner@2.0.3";

const Toaster = ({ ...props } ) => {
  return (
    <Sonner
      theme="light"
      className="toaster group"
      style={
        {
          "--normal-bg": "var(--popover)",
          "--normal-text": "var(--popover-foreground)",
          "--normal-border": "var(--border)",
        }
      }
      {...props}
    />
  );
};

export { Toaster };