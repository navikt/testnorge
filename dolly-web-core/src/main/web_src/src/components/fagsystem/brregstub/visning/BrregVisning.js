import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import { Historikk } from '~/components/ui/historikk/Historikk'

export const BrregVisning = ({ data, loading }) => {
	if (loading) return <Loading label="laster brreg-data" />
	if (!data) return false

	return (
		<div>
			<SubOverskrift label="Brønnøysundregistrene" iconKind="brreg" />
			<div className="person-visning_content">
				<TitleValue title="Understatus" value={data.understatuser} />
				<DollyFieldArray data={data.enheter} header="Enheter" nested>
					{(enhet, idx) => (
						<div className="person-visning_content" key={idx}>
							<TitleValue title="Rollebeskrivelse" value={enhet.rollebeskrivelse} />
							<TitleValue
								title="Registreringsdato"
								value={Formatters.formatStringDates(enhet.registreringsdato)}
							/>
							<TitleValue title="Foretaksnavn" value={enhet.foretaksNavn.navn1} />
							<TitleValue title="Organisasjonsnummer" value={enhet.orgNr} />
						</div>
					)}
				</DollyFieldArray>
			</div>
		</div>
	)
}
