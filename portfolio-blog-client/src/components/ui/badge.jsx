import React from "react";
import { cn } from "../../lib/utils";

export function Badge({ className, ...props }) {
    return (
        <span
            className={cn(
                "inline-flex items-center rounded-full bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-700",
                className
            )}
            {...props}
        />
    );
}