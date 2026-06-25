import { useState, useEffect, useId } from "react";
import './DollySpinner.less'

const FRAME_DURATION = 100; // ms per frame

interface ShadowFrame {
  cx: number
  cy: number
  rx: number
  ry: number
}

interface Frame {
  shadow: ShadowFrame
  legs: string
  bodyY: number
  earOffset: number
}

interface SheepBodyProps {
  bodyY: number
  gradientIdL: string
  gradientIdR: string
}

interface DollySpinnerProps {
  size?: number
  label?: string
}

// All 8 frames as inline SVG path data
const frames: Frame[] = [
  // Frame 1 - standing, legs down
  {
    shadow: { cx: 745.5, cy: 1168, rx: 395.5, ry: 90 },
    legs: `
      <path d="M559 741L667 1030" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M971 741L1079 1030" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M507 741C421.728 768.248 302.346 853.796 507 978" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M944 741C858.728 768.248 739.346 853.796 944 978" stroke="black" stroke-width="55" stroke-linecap="round"/>
    `,
    bodyY: 0,
    earOffset: 93,
  },
  // Frame 2 - jumping up (small)
  {
    shadow: { cx: 745, cy: 1168, rx: 275, ry: 90 },
    legs: `
      <path d="M507 611C421.728 638.248 302.346 723.796 507 848" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M620 611C534.728 638.248 415.346 723.796 620 848" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M944 611C858.728 638.248 739.346 723.796 944 848" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M1057 611C971.728 638.248 852.346 723.796 1057 848" stroke="black" stroke-width="55" stroke-linecap="round"/>
    `,
    bodyY: -93,
    earOffset: 0,
  },
  // Frame 3 - landing variant
  {
    shadow: { cx: 745.5, cy: 1168, rx: 395.5, ry: 90 },
    legs: `
      <path d="M571.953 741L429.19 1014.5" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M972.084 751L811.802 1014.62" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M494 741C408.728 768.248 289.346 853.796 494 978" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M924 741C838.728 768.248 719.346 853.796 924 978" stroke="black" stroke-width="55" stroke-linecap="round"/>
    `,
    bodyY: 0,
    earOffset: 93,
  },
  // Frame 4 - crouching
  {
    shadow: { cx: 767, cy: 1168, rx: 482, ry: 90 },
    legs: `
      <path d="M507 897C421.728 924.248 302.346 1009.8 507 1134" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M620 897C534.728 924.248 415.346 1009.8 620 1134" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M944 897C858.728 924.248 739.346 1009.8 944 1134" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M1057 897C971.728 924.248 852.346 1009.8 1057 1134" stroke="black" stroke-width="55" stroke-linecap="round"/>
    `,
    bodyY: 157,
    earOffset: 250,
  },
  // Frame 5 - standing alt
  {
    shadow: { cx: 745.5, cy: 1168, rx: 395.5, ry: 90 },
    legs: `
      <path d="M402 741L510 1030" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M833 741L941 1030" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M640 741C554.728 768.248 435.346 853.796 640 978" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M1071 741C985.728 768.248 866.346 853.796 1071 978" stroke="black" stroke-width="55" stroke-linecap="round"/>
    `,
    bodyY: 0,
    earOffset: 93,
  },
  // Frame 6 - jumping up (small) again
  {
    shadow: { cx: 745, cy: 1168, rx: 275, ry: 90 },
    legs: `
      <path d="M507 611C421.728 638.248 302.346 723.796 507 848" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M620 611C534.728 638.248 415.346 723.796 620 848" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M944 611C858.728 638.248 739.346 723.796 944 848" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M1057 611C971.728 638.248 852.346 723.796 1057 848" stroke="black" stroke-width="55" stroke-linecap="round"/>
    `,
    bodyY: -93,
    earOffset: 0,
  },
  // Frame 7 - landing variant alt
  {
    shadow: { cx: 745.5, cy: 1168, rx: 395.5, ry: 90 },
    legs: `
      <path d="M428.953 741L286.19 1014.5" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M867.084 751L706.802 1014.62" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M606 741C520.728 768.248 401.346 853.796 606 978" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M1047 741C961.728 768.248 842.346 853.796 1047 978" stroke="black" stroke-width="55" stroke-linecap="round"/>
    `,
    bodyY: 0,
    earOffset: 93,
  },
  // Frame 8 - crouching again
  {
    shadow: { cx: 767, cy: 1168, rx: 482, ry: 90 },
    legs: `
      <path d="M507 897C421.728 924.248 302.346 1009.8 507 1134" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M620 897C534.728 924.248 415.346 1009.8 620 1134" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M944 897C858.728 924.248 739.346 1009.8 944 1134" stroke="black" stroke-width="55" stroke-linecap="round"/>
      <path d="M1057 897C971.728 924.248 852.346 1009.8 1057 1134" stroke="black" stroke-width="55" stroke-linecap="round"/>
    `,
    bodyY: 157,
    earOffset: 250,
  },
];

// The shared body/wool SVG paths (same across all frames, just translated)
function SheepBody({ bodyY, gradientIdL, gradientIdR }: SheepBodyProps) {
  return (
    <g transform={`translate(0, ${bodyY})`}>
      {/* Big wool body */}
      <path
        fillRule="evenodd"
        clipRule="evenodd"
        d="M768.982 312.524C751.935 312.524 736.262 318.402 723.877 328.242C708.357 302.339 680.014 285 647.621 285C605.606 285 570.404 314.169 561.16 353.36C542.353 337.262 517.926 327.539 491.229 327.539C445.06 327.539 405.683 356.617 390.412 397.458C382.552 395.911 374.428 395.1 366.114 395.1C297.016 395.1 241 451.115 241 520.214C241 568.78 268.671 610.883 309.108 631.616C296.299 646.337 288.544 665.571 288.544 686.616C288.544 732.912 326.074 770.443 372.37 770.443C391.847 770.443 409.772 763.8 424.004 752.658C438.743 799.237 482.312 833 533.767 833C582.863 833 624.78 802.262 641.323 758.98C668.096 786.137 705.313 802.972 746.462 802.972C785.73 802.972 821.417 787.641 847.865 762.638C860.567 793.643 891.046 815.484 926.625 815.484C965.197 815.484 997.773 789.816 1008.2 754.629C1023.67 770.557 1045.31 780.453 1069.26 780.453C1116.24 780.453 1154.33 742.363 1154.33 695.376C1154.33 694.583 1154.32 693.792 1154.3 693.005C1208.57 682.004 1249.42 634.031 1249.42 576.517C1249.42 527.083 1219.24 484.697 1176.3 466.779C1176.67 463.372 1176.85 459.911 1176.85 456.408C1176.85 403.202 1133.72 360.07 1080.52 360.07C1061.57 360.07 1043.9 365.538 1029 374.983C1013.98 325.788 968.226 290.005 914.115 290.005C876.492 290.005 842.91 307.302 820.887 334.378C807.711 320.894 789.324 312.524 768.982 312.524Z"
        fill="white"
        stroke="black"
        strokeWidth="14"
      />

      {/* Left ear flap */}
      <g transform="translate(0, 30)">
      <path
        d="M27.528 310C41.2021 276 69.6629 224 109.868 200C152.592 176 211.267 178 246.536 183C255.818 185 261.324 194 258.108 203C246.211 236 219.156 286 174.473 312C130.03 338 73.0103 336 38.6811 330C29.1959 328 23.9175 319 27.528 310Z"
        fill="white"
        stroke="black"
        strokeWidth="14"
      />
      {/* Right ear flap */}
      <path
        d="M644.81 310C631.136 276 602.675 224 562.47 200C519.746 176 461.07 178 425.802 183C416.52 185 411.013 194 414.23 203C426.126 236 453.182 286 497.865 312C542.308 338 599.328 336 633.657 330C643.142 328 648.42 319 644.81 310Z"
        fill="white"
        stroke="black"
        strokeWidth="14"
      />
      </g>

      {/* Face/head */}
      <path
        d="M522 470.921C522 616.172 450.532 682 338.999 682C227.465 682 155 616.172 155 470.921C155 325.67 306.497 156 338.999 156C371.5 156 522 325.67 522 470.921Z"
        fill="#FCBA8E"
        stroke="black"
        strokeWidth="14"
      />

      {/* Head wool */}
      <path
        fillRule="evenodd"
        clipRule="evenodd"
        d="M352.379 128.854C345.038 128.854 338.288 131.385 332.954 135.622C326.271 124.467 314.065 117 300.114 117C282.02 117 266.86 129.562 262.879 146.44C254.78 139.507 244.26 135.32 232.763 135.32C212.88 135.32 195.922 147.842 189.345 165.431C185.96 164.765 182.462 164.416 178.881 164.416C149.123 164.416 125 188.539 125 218.297C125 239.212 136.917 257.344 154.331 266.273C148.815 272.612 145.475 280.896 145.475 289.959C145.475 309.897 161.638 326.059 181.575 326.059C189.963 326.059 197.683 323.199 203.812 318.4C210.159 338.46 228.923 353 251.082 353C272.226 353 290.277 339.763 297.402 321.123C308.932 332.819 324.96 340.068 342.68 340.068C359.592 340.068 374.961 333.466 386.351 322.698C391.821 336.051 404.947 345.457 420.269 345.457C436.881 345.457 450.91 334.402 455.402 319.249C462.061 326.108 471.38 330.37 481.694 330.37C501.929 330.37 518.333 313.966 518.333 293.731C518.333 293.389 518.329 293.049 518.319 292.709C541.691 287.972 559.283 267.312 559.283 242.543C559.283 221.254 546.286 203 527.794 195.284C527.951 193.817 528.032 192.326 528.032 190.817C528.032 167.904 509.457 149.329 486.543 149.329C478.384 149.329 470.776 151.684 464.359 155.751C457.889 134.566 438.185 119.155 414.881 119.155C398.679 119.155 384.216 126.605 374.732 138.265C369.058 132.458 361.139 128.854 352.379 128.854Z"
        fill="white"
        stroke="black"
        strokeWidth="14"
      />

      {/* Eyes */}
      <circle cx="276" cy="430" r="35" fill="black" />
      <ellipse cx="285.419" cy="417.306" rx="7.79533" ry="16.76" transform="rotate(-45.5145 285.419 417.306)" fill="white" />
      <circle cx="402" cy="430" r="35" fill="black" />
      <ellipse cx="411.419" cy="417.306" rx="7.79533" ry="16.76" transform="rotate(-45.5145 411.419 417.306)" fill="white" />

      {/* Nostrils */}
      <path d="M375.792 565.495C348.296 572.938 344.669 608.311 346.751 631.492C346.943 633.638 349.797 633.777 350.402 631.709C360.789 596.2 372.546 591.75 394.79 586.233C407.421 583.101 415.16 554.839 375.792 565.495Z" fill="black" />
      <path d="M302.065 565.495C329.561 572.938 333.189 608.311 331.107 631.492C330.914 633.638 328.06 633.777 327.455 631.709C317.069 596.2 305.311 591.75 283.067 586.233C270.436 583.101 262.697 554.839 302.065 565.495Z" fill="black" />

      {/* Body fur shading */}
      <path d="M237.173 374.238C182.942 367.731 140.462 376.95 126 382.373V417.623C126 417.623 142.348 423.519 147.692 455.584C150.404 471.854 166.673 512.527 223.615 512.527C280.558 512.527 288.692 498.969 302.25 471.854C315.808 444.738 310.385 412.2 334.788 412.2C359.192 412.2 359.192 436.604 367.327 455.584C375.462 474.565 378.173 512.527 451.385 512.527C524.596 512.527 527.308 469.142 530.019 447.45C532.188 430.096 543.577 420.334 549 417.623V382.373C545.385 379.661 522.427 374.238 459.519 374.238C380.885 374.238 359.192 390.507 334.788 387.796C310.385 385.084 304.962 382.373 237.173 374.238Z" fill="black" />

      {/* Gradient belly left */}
      <path d="M299.696 451.864C310.542 419.325 318.677 386.787 231.907 384.075C147.692 377.094 161.407 400.345 158.696 441.018C155.984 481.691 188.523 500.672 231.907 500.672C275.292 500.672 288.85 484.402 299.696 451.864Z" fill={`url(#${gradientIdL})`} />
      {/* Gradient belly right */}
      <path d="M376.6 451.864C365.754 419.325 357.619 386.787 444.388 384.075C528.604 377.094 514.888 400.345 517.6 441.018C520.311 481.691 487.773 500.672 444.388 500.672C401.004 500.672 387.446 484.402 376.6 451.864Z" fill={`url(#${gradientIdR})`} />

      <defs>
        <linearGradient id={gradientIdL} x1="232" y1="383" x2="232" y2="501" gradientUnits="userSpaceOnUse">
          <stop stopColor="#7D7D7D" />
          <stop offset="1" stopColor="#272626" stopOpacity="0" />
        </linearGradient>
        <linearGradient id={gradientIdR} x1="444" y1="383" x2="444" y2="501" gradientUnits="userSpaceOnUse">
          <stop stopColor="#7D7D7D" />
          <stop offset="1" stopColor="#272626" stopOpacity="0" />
        </linearGradient>
      </defs>
    </g>
  );
}

export default function DollySpinner({ size = 200, label = "Loading..." }: DollySpinnerProps) {
  const id = useId();
  const gradientIdL = `${id}-bellyGradL`;
  const gradientIdR = `${id}-bellyGradR`;

  const prefersReducedMotion =
    typeof window !== "undefined" &&
    window.matchMedia("(prefers-reduced-motion: reduce)").matches;

  const [frame, setFrame] = useState(0);

  useEffect(() => {
    if (prefersReducedMotion) return;
    const intervalId = setInterval(() => {
      setFrame((f) => (f + 1) % frames.length);
    }, FRAME_DURATION);
    return () => clearInterval(intervalId);
  }, [prefersReducedMotion]);

  const current = frames[frame];
  const hasLabel = !!label;

  return (
    <div className="dolly-spinner">
      <svg
        width={size}
        height={size}
        viewBox="0 0 1281 1281"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        {...(hasLabel ? { role: "img", "aria-label": label } : { "aria-hidden": "true" })}
      >
        {/* Shadow on ground */}
        <ellipse
          cx={current.shadow.cx}
          cy={current.shadow.cy}
          rx={current.shadow.rx}
          ry={current.shadow.ry}
          fill="#D9D9D9"
        />

        {/* Legs (frame-specific) */}
        <g dangerouslySetInnerHTML={{ __html: current.legs }} />

        {/* Body (translated per frame) */}
        <SheepBody bodyY={current.bodyY} gradientIdL={gradientIdL} gradientIdR={gradientIdR} />
      </svg>

      {hasLabel && (
        <p style={{ margin: 0, fontSize: size * 0.16, color: "#555", fontFamily: "sans-serif" }}>
          {label}
        </p>
      )}
    </div>
  );
}
