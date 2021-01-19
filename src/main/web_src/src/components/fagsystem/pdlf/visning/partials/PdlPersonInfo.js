import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { UtenlandsId } from '~/components/fagsystem/pdlf/visning/partials/UtenlandsId'
import { FalskIdentitet } from '~/components/fagsystem/pdlf/visning/partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from '~/components/fagsystem/pdlf/visning/partials/KontaktinformasjonForDoedsbo'

export const PdlPersonInfo = ({ data, visTittel = true }) => {
	if (!data) {
		return null
	}
	return (
		<ErrorBoundary>
			<div>
				{visTittel && <SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />}
				<div className="person-visning_content">
					<TitleValue title="Fornavn" value={data.navn[0].fornavn} />
					<TitleValue title="Mellomnavn" value={data.navn[0].mellomnavn} />
					<TitleValue title="Etternavn" value={data.navn[0].etternavn} />
					<TitleValue title="Kjønn" value={data.kjoenn[0].kjoenn} />
					<TitleValue title="Sivilstand" value={data.sivilstand[0].type} />
					<TitleValue title="Fødselsdato" value={data.foedsel[0].foedselsdato} />
					<h4 style={{ marginTop: '0px' }}>Adressebeskyttelse</h4>
					<div className="person-visning_content">
						<TitleValue
							title="Gradering (Diskresjonskode)"
							value={data.adressebeskyttelse[0].gradering}
						/>
						<TitleValue
							title="Kilde"
							value={data.adressebeskyttelse[0].folkeregistermetadata.kilde}
						/>
						<TitleValue
							title="Gyldighetstidspunkt"
							value={data.adressebeskyttelse[0].folkeregistermetadata.gyldighetstidspunkt}
						/>
					</div>
				</div>
				<UtenlandsId data={data.utenlandskIdentifikasjonsnummer} loading={false} />
				<FalskIdentitet data={data.falskIdentitet} loading={false} />
				<KontaktinformasjonForDoedsbo data={data.kontaktinformasjonForDoedsbo} loading={false} />
			</div>
		</ErrorBoundary>
	)
}
