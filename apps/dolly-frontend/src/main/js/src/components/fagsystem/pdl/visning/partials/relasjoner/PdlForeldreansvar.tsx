import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import _ from 'lodash'
import { formatDate } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'
import { Foreldreansvar } from '@/components/fagsystem/pdlf/PdlTypes'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'

type PdlForeldreansvarProps = {
	data: Array<Foreldreansvar>
}

type VisningProps = {
	data: Foreldreansvar
	idx: number
}

const PdlForeldreansvarVisning = ({ data, idx }: VisningProps) => {
	return (
		<div className="person-visning_content" key={idx}>
			<TitleValue title="Hvem har ansvaret" value={_.capitalize(data.ansvar)} />
			<TitleValue title="Ansvarssubjekt" value={data.ansvarssubjekt} />
			<TitleValue title="Ansvarlig" value={data.ansvarlig} />
			{data.ansvarligUtenIdentifikator && (
				<div className="flexbox--full-width">
					<h4 style={{ marginTop: '5px' }}>Ansvarlig uten identifikator</h4>
					<div className="person-visning_content" key={idx}>
						<TitleValue
							title="Fødselsdato"
							value={formatDate(data.ansvarligUtenIdentifikator.foedselsdato)}
						/>
						<TitleValue title="Kjønn" value={data.ansvarligUtenIdentifikator.kjoenn} />
						<TitleValue title="Fornavn" value={data.ansvarligUtenIdentifikator.navn?.fornavn} />
						<TitleValue
							title="Mellomnavn"
							value={data.ansvarligUtenIdentifikator.navn?.mellomnavn}
						/>
						<TitleValue title="Etternavn" value={data.ansvarligUtenIdentifikator.navn?.etternavn} />
						<TitleValue
							title="Statsborgerskap"
							value={data.ansvarligUtenIdentifikator.statsborgerskap}
							kodeverk={AdresseKodeverk.StatsborgerskapLand}
						/>
					</div>
				</div>
			)}
		</div>
	)
}

export const PdlForeldreansvar = ({ data }: PdlForeldreansvarProps) => {
	if (!data || data.length === 0) {
		return null
	}

	const gyldigeForeldreansvar = data.filter(
		(foreldreansvar: Foreldreansvar) => !foreldreansvar.metadata?.historisk,
	)
	const historiskeForeldreansvar = data.filter(
		(foreldreansvar: Foreldreansvar) => foreldreansvar.metadata?.historisk,
	)

	return (
		<div>
			<SubOverskrift label="Foreldreansvar" iconKind="foreldreansvar" />
			<ArrayHistorikk
				component={PdlForeldreansvarVisning}
				data={gyldigeForeldreansvar}
				historiskData={historiskeForeldreansvar}
				header={''}
			/>
		</div>
	)
}
