import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const BrregVisning = ({ data, loading }) => {
	if (loading) return <Loading label="laster brreg-data" />
	if (!data) return false

	return (
		<div>
			<SubOverskrift label="Brønnøysundregistrene" iconKind="brreg" />
			<div className="person-visning_content">
				<TitleValue
					title="Understatuser"
					value={data.understatuser && data.understatuser.join(', ')}
				/>
				<DollyFieldArray data={data.enheter} header="Enhet">
					{(enhet, idx) => (
						<div className="person-visning_content" key={idx}>
							<TitleValue title="Rolle" value={enhet.rollebeskrivelse} />
							<TitleValue
								title="Registreringsdato"
								value={Formatters.formatStringDates(enhet.registreringsdato)}
							/>
							<TitleValue title="Organisasjonsnummer" value={enhet.orgNr} />
							<TitleValue title="Foretaksnavn" value={enhet.foretaksNavn.navn1} />
							{/* TODO: Vise personroller som nested fieldArray. Vent på brreg-endepunkt */}
						</div>
					)}
				</DollyFieldArray>
			</div>
		</div>
	)
}
