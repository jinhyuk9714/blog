import React from "react";
import { Slot } from "@radix-ui/react-slot";
import { cn } from "../../lib/utils";

const base =
    "inline-flex items-center justify-center rounded-xl px-3 py-2 text-sm font-medium transition " +
    "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed";

const variants = {
    solid: "bg-indigo-600 text-white hover:bg-indigo-700",
    outline: "border bg-white hover:bg-gray-50",
    ghost: "hover:bg-gray-100",
};

export function Button({ asChild, variant = "solid", className, ...props }) {
    const Comp = asChild ? Slot : "button";
    return <Comp className={cn(base, variants[variant], className)} {...props} />;
}