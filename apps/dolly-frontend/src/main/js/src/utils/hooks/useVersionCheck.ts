import { useEffect, useRef, useState } from 'react'

const POLL_INTERVAL_MS = 60_000

const getCurrentCommitHash = (): string => {
	try {
		return (process.env.COMMIT_HASH || '').trim()
	} catch {
		return ''
	}
}

export const CURRENT_COMMIT_HASH = getCurrentCommitHash()

interface VersionResponse {
	commitHash: string
}

export const useVersionCheck = (overrideCommitHash?: string) => {
	const [isNewVersionAvailable, setIsNewVersionAvailable] = useState(false)
	const detectedRef = useRef(false)
	const commitHash = overrideCommitHash ?? CURRENT_COMMIT_HASH

	useEffect(() => {
		if (!commitHash) return

		let intervalId: ReturnType<typeof setInterval> | null = null

		const checkVersion = () => {
			if (detectedRef.current) return

			fetch(`/version.json?_t=${Date.now()}`, { cache: 'no-store' })
				.then((response) => {
					if (!response.ok) return
					return response.json()
				})
				.then((data: VersionResponse | undefined) => {
					if (data?.commitHash && data.commitHash !== commitHash) {
						detectedRef.current = true
						setIsNewVersionAvailable(true)
						if (intervalId) {
							clearInterval(intervalId)
							intervalId = null
						}
					}
				})
				.catch(() => {})
		}

		checkVersion()
		intervalId = setInterval(checkVersion, POLL_INTERVAL_MS)

		const handleVisibilityChange = () => {
			if (document.visibilityState === 'visible') {
				checkVersion()
			}
		}

		document.addEventListener('visibilitychange', handleVisibilityChange)

		return () => {
			if (intervalId) {
				clearInterval(intervalId)
			}
			document.removeEventListener('visibilitychange', handleVisibilityChange)
		}
	}, [commitHash])

	return { isNewVersionAvailable }
}
