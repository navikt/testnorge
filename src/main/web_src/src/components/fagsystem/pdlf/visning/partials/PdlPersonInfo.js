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

	const adressebeskyttelse = data.adressebeskyttelse ? data.adressebeskyttelse[0] : null
	const personNavn = data.navn ? data.navn[0] : null
	const personKjoenn = data.kjoenn ? data.kjoenn[0] : null
	const personSivilstand = data.sivilstand ? data.sivilstand[0] : null
	const personFoedsel = data.foedsel ? data.foedsel[0] : null

	return (
		<ErrorBoundary>
			<div>
				{visTittel && <SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />}
				<div className="person-visning_content">
					<TitleValue title="Fornavn" value={personNavn && personNavn.fornavn} />
					<TitleValue title="Mellomnavn" value={personNavn && personNavn.mellomnavn} />
					<TitleValue title="Etternavn" value={personNavn && personNavn.etternavn} />
					<TitleValue title="Kjønn" value={personKjoenn && personKjoenn.kjoenn} />
					<TitleValue title="Sivilstand" value={personSivilstand && personSivilstand.type} />
					<TitleValue title="Fødselsdato" value={personFoedsel && personFoedsel.foedselsdato} />
					<h4 style={{ marginTop: '0px' }}>Adressebeskyttelse</h4>
					<div className="person-visning_content">
						<TitleValue
							title="Gradering (Diskresjonskode)"
							value={adressebeskyttelse && adressebeskyttelse.gradering}
						/>
						<TitleValue
							title="Kilde"
							value={
								adressebeskyttelse &&
								adressebeskyttelse.folkeregistermetadata &&
								adressebeskyttelse.folkeregistermetadata.kilde
							}
						/>
						<TitleValue
							title="Gyldighetstidspunkt"
							value={
								adressebeskyttelse &&
								adressebeskyttelse.folkeregistermetadata &&
								adressebeskyttelse.folkeregistermetadata.gyldighetstidspunkt
							}
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
