import React from "react";
import { cn } from "../../lib/utils";

export const Textarea = React.forwardRef(function Textarea({ className, rows = 4, ...props }, ref) {
    return (
        <textarea
            ref={ref}
            rows={rows}
            className={cn(
                "w-full rounded-xl border px-3 py-2 text-sm shadow-sm",
                "focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200",
                className
            )}
            {...props}
        />
    );
});