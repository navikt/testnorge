import DollyService from '@/service/services/dolly/DollyService'
import Button from '@/components/ui/button/Button'
import useBoolean from '@/utils/hooks/useBoolean'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { GjenopprettModal } from '@/components/bestilling/gjenopprett/GjenopprettModal'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString } from '@/utils/DataFormatter'
import { useBestillingById } from '@/utils/hooks/useBestilling'

type GjenopprettProps = {
	ident: {
		bestillingId: Array<number>
		ident: string
		bestillinger: Array<any>
	}
}

type Values = {
	environments: Array<string>
}

export const GjenopprettPerson = ({ ident }: GjenopprettProps) => {
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)
	const mutate = useMatchMutate()

	if (!ident) {
		return null
	}

	const getBestilteMiljoer = () => {
		const miljoer: Array<string> = []
		ident.bestillingId.forEach((id) => {
			const { bestilling } = useBestillingById(id.toString())
			bestilling?.environments?.forEach((miljo) => {
				if (!miljoer.includes(miljo)) {
					miljoer.push(miljo)
				}
			})
		})
		return miljoer
	}

	const bestilteMiljoer = getBestilteMiljoer()

	const handleSubmit = async (values: Values) => {
		const filteredEnvs = values.environments?.filter((env: string) => env !== 'q5')

		let miljoerString = ''
		filteredEnvs.forEach((env: string, i: number) => {
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
			<Button onClick={openGjenopprettModal} kind="synchronize">
				GJENOPPRETT PERSON
			</Button>
			{isGjenopprettModalOpen && (
				<GjenopprettModal
					gjenopprettHeader={gjenopprettHeader}
					environments={bestilteMiljoer}
					submitFormik={handleSubmit}
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
