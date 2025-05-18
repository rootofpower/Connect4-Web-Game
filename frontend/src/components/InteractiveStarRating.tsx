import React, { useState, useRef, MouseEvent as ReactMouseEvent } from 'react';
import { StarIcon } from 'lucide-react';

interface InteractiveStarRatingProps {
    currentRating: number | null;
    onRatingChange: (newRating: number) => void;
    totalStars?: number;
    starSize?: number;
    precision?: number;
    activeStarColor?: string;
    inactiveStarColor?: string;
    disabled?: boolean;
    minRating?: number;
}

const InteractiveStarRating: React.FC<InteractiveStarRatingProps> = ({
    currentRating,
    onRatingChange,
    totalStars = 5,
    starSize = 28,
    precision = 0.01,
    activeStarColor = 'fill-yellow-500 text-yellow-500',
    inactiveStarColor = 'text-gray-400',
    disabled = false,
    minRating = 1,
}) => {
    const [hoverRating, setHoverRating] = useState<number | null>(null);
    const ratingContainerRef = useRef<HTMLDivElement>(null);

    const getRatingFromMouseEvent = (event: ReactMouseEvent<HTMLDivElement>): number => {
        if (!ratingContainerRef.current || disabled) return 0;

        const rect = ratingContainerRef.current.getBoundingClientRect();
        const mouseX = event.clientX - rect.left;
        
        if (rect.width === 0) return 0;

        const rawPercentage = Math.min(1, Math.max(0, mouseX / rect.width));
        const rawRating = rawPercentage * totalStars;
        let preciseRating = Math.round(rawRating / precision) * precision;

        if (rawPercentage > 0 && preciseRating < precision) {
            preciseRating = precision;
        }
        
        return Math.min(totalStars, Math.max(0, preciseRating));
    };

    const handleMouseMove = (event: ReactMouseEvent<HTMLDivElement>) => {
        if (disabled) return;
        setHoverRating(getRatingFromMouseEvent(event));
    };

    const handleMouseLeave = () => {
        if (disabled) return;
        setHoverRating(null);
    };

    const handleClick = (event: ReactMouseEvent<HTMLDivElement>) => {
        if (disabled) return;
        let clickedRating = getRatingFromMouseEvent(event);

        if (clickedRating > 0 && clickedRating < minRating) {
            clickedRating = minRating;
        }

        if (clickedRating >= minRating) {
            onRatingChange(clickedRating);
        } else if (clickedRating > 0 && minRating === 0) {
             onRatingChange(clickedRating);
        }
    };

    const displayValue = hoverRating !== null ? hoverRating : (currentRating !== null ? currentRating : 0);

    return (
        <div
            ref={ratingContainerRef}
            className={`inline-flex items-center ${disabled ? 'cursor-not-allowed opacity-70' : 'cursor-pointer'}`}
            onMouseMove={handleMouseMove}
            onMouseLeave={handleMouseLeave}
            onClick={handleClick}
            role="slider"
            aria-valuenow={displayValue}
            aria-valuemin={0}
            aria-valuemax={totalStars}
            aria-label={`Rating: ${displayValue.toFixed(2)} out of ${totalStars} stars. Click to set a new rating.`}
            tabIndex={disabled ? -1 : 0}
        >
            {Array.from({ length: totalStars }, (_, index) => {
                const fill = Math.max(0, Math.min(1, displayValue - index));
                return (
                    <div
                        key={index}
                        className="relative"
                        style={{ width: starSize, height: starSize }}
                    >
                        <StarIcon
                            className={`absolute top-0 left-0 ${inactiveStarColor}`}
                            style={{ width: starSize, height: starSize }}
                        />
                        {fill > 0 && (
                            <div
                                className="absolute top-0 left-0 h-full overflow-hidden"
                                style={{ width: `${fill * 100}%` }}
                            >
                                <StarIcon
                                    className={`block ${activeStarColor}`}
                                    style={{ width: starSize, height: starSize }}
                                />
                            </div>
                        )}
                    </div>
                );
            })}
        </div>
    );
};

export default InteractiveStarRating;