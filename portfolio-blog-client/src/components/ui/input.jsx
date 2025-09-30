import React from "react";
import { cn } from "../../lib/utils";

export const Input = React.forwardRef(function Input({ className, ...props }, ref) {
    return (
        <input
            ref={ref}
            className={cn(
                "w-full rounded-xl border px-3 py-2 text-sm shadow-sm",
                "focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200",
                className
            )}
            {...props}
        />
    );
});