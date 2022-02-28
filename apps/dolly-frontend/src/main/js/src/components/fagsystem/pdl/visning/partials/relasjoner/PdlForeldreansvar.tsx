import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import _capitalize from 'lodash/capitalize'
import Formatters from '~/utils/DataFormatter'
import { AdresseKodeverk } from '~/config/kodeverk'

export const PdlForeldreansvar = ({ data }) => {
	if (!data || data.length === 0) return null

	const PdlForeldreansvarVisning = ({ item, idx }) => {
		return (
			<div className="person-visning_content" key={idx}>
				<TitleValue title="Hvem har ansvaret" value={_capitalize(item.ansvar)} />
				<TitleValue title="Ansvarlig" value={item.ansvarlig} />
				{item.ansvarligUtenIdentifikator && (
					<div className="flexbox--full-width">
						<h4 style={{ marginTop: '5px' }}>Ansvarlig uten identifikator</h4>
						<div className="person-visning_content" key={idx}>
							<TitleValue
								title="Fødselsdato"
								value={Formatters.formatDate(item.ansvarligUtenIdentifikator.foedselsdato)}
							/>
							<TitleValue title="Kjønn" value={item.ansvarligUtenIdentifikator.kjoenn} />
							<TitleValue title="Fornavn" value={item.ansvarligUtenIdentifikator.navn?.fornavn} />
							<TitleValue
								title="Mellomnavn"
								value={item.ansvarligUtenIdentifikator.navn?.mellomnavn}
							/>
							<TitleValue
								title="Etternavn"
								value={item.ansvarligUtenIdentifikator.navn?.etternavn}
							/>
							<TitleValue
								title="Statsborgerskap"
								value={item.ansvarligUtenIdentifikator.statsborgerskap}
								kodeverk={AdresseKodeverk.StatsborgerskapLand}
							/>
						</div>
					</div>
				)}
			</div>
		)
	}

	return (
		<div>
			<SubOverskrift label="Foreldreansvar" iconKind="foreldreansvar" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(item, idx: number) => <PdlForeldreansvarVisning item={item} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
