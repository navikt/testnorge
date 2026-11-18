import {
	useArenaEnvironments,
	useDokarkivEnvironments,
	useInstEnvironments,
	usePensjonEnvironments,
} from '@/utils/hooks/useEnvironments'
import { arrayToString } from '@/utils/DataFormatter'
import { filterMiljoe } from '@/components/miljoVelger/MiljoVelgerUtils'
import { InfoCard } from '@navikt/ds-react'
import { InformationSquareFillIcon } from '@navikt/aksel-icons'

const SKATTEKORT_MILJOER = ['q1', 'q2']

type MiljoeInfoProps = {
	bestillingsdata: Record<string, any>
	dollyEnvironments: any[]
	tilgjengeligeMiljoer: string[] | null
}

export const MiljoeInfo = ({
	bestillingsdata,
	dollyEnvironments,
	tilgjengeligeMiljoer,
}: MiljoeInfoProps) => {
	const { arenaEnvironments, loading: loadingArena, error: errorArena } = useArenaEnvironments()
	const {
		pensjonEnvironments,
		loading: loadingPensjon,
		error: errorPensjon,
	} = usePensjonEnvironments()
	const {
		instEnvironments,
		kdiEnvironments,
		loading: loadingInst,
		error: errorInst,
	} = useInstEnvironments()
	const {
		dokarkivEnvironments,
		loading: loadingDokarkiv,
		error: errorDokarkiv,
	} = useDokarkivEnvironments()

	const { instdata, instdataKdi, arenaforvalter, pensjonforvalter, dokarkiv, skattekort } =
		bestillingsdata
	if (
		!instdata &&
		!instdataKdi &&
		!arenaforvalter &&
		!pensjonforvalter &&
		!dokarkiv &&
		!skattekort
	) {
		return null
	}
	const getMiljoer = (environments: string[] | undefined, loading?: boolean, error?: any) => {
		if (loading) {
			return 'Laster tilgjengelige miljøer ...'
		} else if (error && (!environments || environments.length === 0)) {
			return 'Noe gikk galt i henting av miljøer'
		} else {
			const filtrerteMiljoer = filterMiljoe(dollyEnvironments, environments, tilgjengeligeMiljoer)
			if (filtrerteMiljoer.length === 0) {
				return 'Du har ikke tilgang til påkrevde miljøer. Ta kontakt med team Dolly for hjelp.'
			}
			return arrayToString(filtrerteMiljoer)
		}
	}

	return (
		<>
			<InfoCard data-color="info" size={'small'} style={{ marginBottom: '20px' }}>
				<InfoCard.Message icon={<InformationSquareFillIcon aria-hidden />}>
					Du har valgt egenskaper som ikke blir distribuert til alle miljøer. For hver av følgende
					egenskaper må derfor ett eller flere av miljøene under velges:
					<ul style={{ margin: '7px 0' }}>
						{arenaforvalter && (
							<li>
								Arena:&nbsp;
								<span>{getMiljoer(arenaEnvironments, loadingArena, errorArena)}</span>
							</li>
						)}
						{dokarkiv && (
							<li>
								Dokarkiv:&nbsp;
								<span>{getMiljoer(dokarkivEnvironments, loadingDokarkiv, errorDokarkiv)}</span>
							</li>
						)}
						{instdata && (
							<li>
								Institusjonsopphold:&nbsp;
								<span>{getMiljoer(instEnvironments, loadingInst, errorInst)}</span>
							</li>
						)}
						{instdataKdi && (
							<li>
								KDI-meldinger:&nbsp;
								<span>{getMiljoer(kdiEnvironments, loadingInst, errorInst)}</span>
							</li>
						)}
						{(pensjonforvalter?.inntekt ||
							pensjonforvalter?.tp ||
							pensjonforvalter?.alderspensjon) && (
							<li>
								Pensjon ({pensjonforvalter?.inntekt && 'POPP'}
								{pensjonforvalter?.inntekt && pensjonforvalter?.tp && ', '}
								{pensjonforvalter?.tp && 'TP'}
								{(pensjonforvalter?.inntekt || pensjonforvalter?.tp) &&
									pensjonforvalter?.alderspensjon &&
									', '}
								{pensjonforvalter?.alderspensjon && 'PESYS'}
								):&nbsp;
								<span>{getMiljoer(pensjonEnvironments, loadingPensjon, errorPensjon)}</span>
							</li>
						)}
						{pensjonforvalter?.uforetrygd && (
							<li>
								Uføretrygd:&nbsp;
								<span>{getMiljoer(pensjonEnvironments)}</span>
							</li>
						)}
						{pensjonforvalter?.pensjonsavtale && (
							<li>
								Pensjonsavtale (PEN):&nbsp;
								<span>{getMiljoer(pensjonEnvironments)}</span>
							</li>
						)}
						{skattekort && (
							<li>
								Skattekort:&nbsp;
								<span>{getMiljoer(SKATTEKORT_MILJOER)}</span>
							</li>
						)}
					</ul>
				</InfoCard.Message>
			</InfoCard>
			{pensjonforvalter && bestillingsdata?.environments?.includes('q4') && (
				<InfoCard data-color="info" style={{ marginTop: 20 }}>
					<InfoCard.Message icon={<InformationSquareFillIcon aria-hidden />}>
						Innsending av testdata til pensjon er for øyeblikket ikke støttet i Q4, bestillingen vil
						bli sendt til Q1 <br />
					</InfoCard.Message>
				</InfoCard>
			)}
			{skattekort &&
				bestillingsdata?.environments?.length > 0 &&
				bestillingsdata?.environments?.every((env) => env === 'q4') && (
					<InfoCard data-color="info" style={{ marginTop: 20 }}>
						<InfoCard.Message icon={<InformationSquareFillIcon aria-hidden />}>
							Innsending av skattekort er for øyeblikket ikke støttet i Q4, bestillingen vil bli
							sendt til Q2.
						</InfoCard.Message>
					</InfoCard>
				)}
		</>
	)
}
