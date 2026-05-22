import DollyService from '@/service/services/dolly/DollyService'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_BACKEND_TRANSAKSJONID,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { GjenopprettModal } from '@/components/bestilling/gjenopprett/GjenopprettModal'
import {
	useBestilteMiljoerAlleFagsystemer,
	useIkkeFerdigBestillingerGruppe,
} from '@/utils/hooks/useBestilling'
import { useEffect, useRef } from 'react'
import { useSideStoerrelse } from '@/utils/hooks/useSideStoerrelse'
import { useReduxSelector } from '@/utils/hooks/useRedux'

type GjenopprettProps = {
	ident: {
		bestillingId: Array<number>
		ident: string
		bestillinger: Array<any>
	}
	gruppeId: string | number
	onGjenopprettDone?: () => void
}

type Values = {
	environments: Array<string>
}

export const GjenopprettPerson = ({ ident, gruppeId, onGjenopprettDone }: GjenopprettProps) => {
	const bestillinger = ident?.bestillingId?.map((id) => id?.toString())
	const { bestilteMiljoer } = useBestilteMiljoerAlleFagsystemer(bestillinger)
	const { sideStoerrelse } = useSideStoerrelse()
	const sidetall = useReduxSelector((state) => state?.finnPerson?.sidetall) || 0

	const { bestillingerById: ikkeFerdigBestillinger } = useIkkeFerdigBestillingerGruppe(
		gruppeId,
		'personer',
		sidetall,
		sideStoerrelse,
	)

	const mutate = useMatchMutate()

	const ikkeFerdigCount = ikkeFerdigBestillinger ? Object.keys(ikkeFerdigBestillinger).length : null
	const prevCountRef = useRef<number | null>(null)

	useEffect(() => {
		if (prevCountRef.current !== null && prevCountRef.current > 0 && ikkeFerdigCount === 0) {
			mutate(REGEX_BACKEND_GRUPPER)
			mutate(REGEX_BACKEND_BESTILLINGER)
			mutate(REGEX_BACKEND_TRANSAKSJONID)
			onGjenopprettDone?.()
		}
		prevCountRef.current = ikkeFerdigCount
	}, [ikkeFerdigCount])

	if (!ident) {
		return null
	}

	const handleSubmit = async (values: Values) => {
		let miljoerString = ''
		values.environments.forEach((env: string, i: number) => {
			if (i === 0) {
				miljoerString += `?miljoer=${env}`
			} else {
				miljoerString += `&miljoer=${env}`
			}
		})

		await DollyService.gjenopprettPerson(ident.ident, miljoerString).then(() => {
			mutate(REGEX_BACKEND_BESTILLINGER)
		})
	}

	const getBestillingerSamlet = () => {
		const samlet = {
			bestilling: {},
		}
		ident.bestillinger?.map((best) => {
			Object.assign(samlet.bestilling, best?.bestilling)
		})
		return samlet
	}
	const bestillingerSamlet = getBestillingerSamlet()

	return (
		<>
			<GjenopprettModal
				environments={bestilteMiljoer}
				submitForm={handleSubmit}
				title={`Gjenopprett person: ${ident.ident}`}
				bestilling={bestillingerSamlet}
				bestilteMiljoer={bestilteMiljoer}
			/>
		</>
	)
}
