import { Alert } from '@navikt/ds-react'
import {
	useArenaEnvironments,
	useDokarkivEnvironments,
	useInstEnvironments,
	usePensjonEnvironments,
} from '@/utils/hooks/useEnvironments'
import { arrayToString } from '@/utils/DataFormatter'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { filterMiljoe } from '@/components/miljoVelger/MiljoVelgerUtils'

export const MiljoeInfo = ({ bestillingsdata, dollyEnvironments, tilgjengeligeMiljoer }) => {
	const { arenaEnvironments, loading: loadingArena, error: errorArena } = useArenaEnvironments()
	const {
		pensjonEnvironments,
		loading: loadingPensjon,
		error: errorPensjon,
	} = usePensjonEnvironments()
	const { instEnvironments, loading: loadingInst, error: errorInst } = useInstEnvironments()
	const {
		dokarkivEnvironments,
		loading: loadingDokarkiv,
		error: errorDokarkiv,
	} = useDokarkivEnvironments()
	const { instdata, arenaforvalter, pensjonforvalter, sykemelding, dokarkiv } = bestillingsdata
	if (!instdata && !arenaforvalter && !pensjonforvalter && !sykemelding && !dokarkiv) {
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
			<StyledAlert variant={'info'}>
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

					{sykemelding && <li>Sykemelding: q1</li>}
				</ul>
			</StyledAlert>
			{pensjonforvalter && bestillingsdata?.environments?.includes('q4') && (
				<Alert variant={'info'} style={{ marginTop: 20 }}>
					Innsending av testdata til pensjon er for øyeblikket ikke støttet i Q4, bestillingen vil
					bli sendt til Q1 <br />
				</Alert>
			)}
		</>
	)
}
