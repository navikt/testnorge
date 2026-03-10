import DollyService from '@/service/services/dolly/DollyService'
import Button from '@/components/ui/button/Button'
import useBoolean from '@/utils/hooks/useBoolean'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	REGEX_TESTNAV,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { GjenopprettModal } from '@/components/bestilling/gjenopprett/GjenopprettModal'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString } from '@/utils/DataFormatter'
import {
	useBestilteMiljoerAlleFagsystemer,
	useIkkeFerdigBestillingerGruppe,
} from '@/utils/hooks/useBestilling'
import { useEffect, useRef } from 'react'
import { useSideStoerrelse } from '@/utils/hooks/useSideStoerrelse'

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
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)

	const { bestillingerById: ikkeFerdigBestillinger } = useIkkeFerdigBestillingerGruppe(
		gruppeId,
		'personer',
		0, //TODO Get sidetall form state
		sideStoerrelse,
	)

	const mutate = useMatchMutate()

	const ikkeFerdigCount = ikkeFerdigBestillinger ? Object.keys(ikkeFerdigBestillinger).length : null
	const prevCountRef = useRef<number | null>(null)

	useEffect(() => {
		if (prevCountRef.current !== null && prevCountRef.current > 0 && ikkeFerdigCount === 0) {
			mutate(REGEX_BACKEND_GRUPPER)
			mutate(REGEX_BACKEND_BESTILLINGER)
			mutate(REGEX_TESTNAV)
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

		await DollyService.gjenopprettPerson(ident.ident, miljoerString).then((res) => {
			mutate(REGEX_BACKEND_BESTILLINGER)
			closeGjenoprettModal()
		})
	}

	const gjenopprettHeader = (
		<div style={{ paddingLeft: 20, paddingRight: 20 }}>
			<h1>Gjenopprett person {ident.ident}</h1>
			{bestilteMiljoer?.length > 0 && (
				<>
					<br />
					<TitleValue title="Bestilt miljø" value={arrayToString(bestilteMiljoer)?.toUpperCase()} />
					<hr />
				</>
			)}
		</div>
	)

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
			<Button
				onClick={openGjenopprettModal}
				kind="synchronize"
				loading={ikkeFerdigBestillinger && Object.keys(ikkeFerdigBestillinger).length > 0}
			>
				GJENOPPRETT
			</Button>
			{isGjenopprettModalOpen && (
				<GjenopprettModal
					gjenopprettHeader={gjenopprettHeader}
					environments={bestilteMiljoer}
					submitForm={handleSubmit}
					closeModal={() => {
						closeGjenoprettModal()
						mutate(REGEX_BACKEND_GRUPPER)
					}}
					bestilling={bestillingerSamlet}
				/>
			)}
		</>
	)
}
