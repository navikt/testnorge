import { Links, Meta, Outlet, Scripts, ScrollRestoration } from 'react-router'

// Import all CSS først
import '@navikt/ds-css'
import '@/styles/main.less'

export function Layout({ children }: { children: React.ReactNode }) {
	return (
		<html lang="en">
			<head>
				<link href="../favicon.ico" rel="icon" />
				<meta charSet="UTF-8" />
				<meta name="viewport" content="width=device-width, initial-scale=1.0" />
				<title>Dolly</title>
				<Meta />
				<Links />
			</head>
			<body>
				{children}
				<ScrollRestoration />
				<Scripts />
			</body>
		</html>
	)
}

export default function Root() {
	return <Outlet />
}
