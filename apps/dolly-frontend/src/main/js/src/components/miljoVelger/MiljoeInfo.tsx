import * as _ from 'lodash-es'
import { Alert } from '@navikt/ds-react'
import {
	useArenaEnvironments,
	useDokarkivEnvironments,
	useInstEnvironments,
	usePensjonEnvironments,
} from '@/utils/hooks/useEnvironments'
import Formatters from '@/utils/DataFormatter'

export const MiljoeInfo = ({ bestillingsdata, dollyEnvironments }) => {
	const { arenaEnvironments, loading: loadingArena, error: errorArena } = useArenaEnvironments()
	const {
		pensjonEnvironments,
		loading: loadingPensjon,
		error: errorPensjon,
	} = usePensjonEnvironments()
	const { instEnvironments, loading: loadingInst, error: errorInst } = useInstEnvironments()
	const { dokarkivEnvironments, loading: loadingDokarkiv } = useDokarkivEnvironments()
	const { instdata, pdldata, arenaforvalter, pensjonforvalter, sykemelding, dokarkiv } =
		bestillingsdata
	if (
		!instdata &&
		!arenaforvalter &&
		!pensjonforvalter &&
		!sykemelding &&
		!dokarkiv &&
		!_.get(pdldata, 'bostedsadresse') &&
		!_.get(pdldata, 'fullmakt') &&
		!_.get(pdldata, 'falskIdentitet') &&
		!_.get(pdldata, 'utenlandskIdentifikasjonsnummer') &&
		!_.get(pdldata, 'kontaktinformasjonForDoedsbo')
	) {
		return null
	}

	const getMiljoer = (environments, loading, error) => {
		if (loading) {
			return 'Laster tilgjengelige miljøer..'
		} else if (error && (!environments || environments.length === 0)) {
			return 'Noe gikk galt i henting av miljøer'
		} else {
			return Formatters.arrayToString(filterMiljoe(dollyEnvironments, environments))
		}
	}

	return (
		<>
			<Alert variant={'info'}>
				Du har valgt egenskaper som ikke blir distribuert til alle miljøer. For hver av følgende
				egenskaper må derfor ett eller flere av miljøene under velges:
				<ul>
					{instdata && (
						<li>
							Institusjonsopphold:&nbsp;
							<span>{getMiljoer(instEnvironments, loadingInst, errorInst)}</span>
						</li>
					)}
					{arenaforvalter && (
						<li>
							Arena:&nbsp;
							<span>{getMiljoer(arenaEnvironments, loadingArena, errorArena)}</span>
						</li>
					)}

					{(pensjonforvalter?.inntekt || pensjonforvalter?.tp || pensjonforvalter?.alderspensjon) && (
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

					{sykemelding && <li>Sykemelding: Q1 må velges</li>}

					{dokarkiv && (
						<li>
							Dokarkiv:&nbsp;
							<span>
								{loadingDokarkiv
									? 'Laster tilgjengelige miljøer..'
									: Formatters.arrayToString(dokarkivEnvironments)}
							</span>
						</li>
					)}
				</ul>
			</Alert>
			{pensjonforvalter && bestillingsdata?.environments?.includes('q4') && (
				<Alert variant={'info'} style={{ marginTop: 20 }}>
					Innsending av testdata til pensjon er for øyeblikket ikke støttet i Q4, bestillingen vil
					bli sendt til Q1 <br />
				</Alert>
			)}
		</>
	)
}

export const filterMiljoe = (dollyMiljoe, utvalgteMiljoer) => {
	if (!utvalgteMiljoer) return []
	const dollyMiljoeArray = flatDollyMiljoe(dollyMiljoe)

	//Filtrerer bort de miljøene som er tilgjengelige for fagsystemene eller en mal,
	//men ikke Dolly per dags dato
	return utvalgteMiljoer.filter((miljoe) => dollyMiljoeArray.includes(miljoe))
}

const flatDollyMiljoe = (dollymiljoe) => {
	const miljoeArray = []
	Object.values(dollymiljoe).forEach((miljoeKat) =>
		miljoeKat.forEach((miljoe) => miljoeArray.push(miljoe.id))
	)
	return miljoeArray
}