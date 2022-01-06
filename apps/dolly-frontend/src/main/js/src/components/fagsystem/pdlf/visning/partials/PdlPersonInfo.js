import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { UtenlandsId } from '~/components/fagsystem/pdlf/visning/partials/UtenlandsId'
import { FalskIdentitet } from '~/components/fagsystem/pdlf/visning/partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from '~/components/fagsystem/pdlf/visning/partials/KontaktinformasjonForDoedsbo'
import Formatters from '~/utils/DataFormatter'

export const PdlPersonInfo = ({ data, visTittel = true }) => {
	if (!data) {
		return null
	}

	const personNavn = data.navn?.length > 0 ? data.navn[0] : null
	const personKjoenn = data.kjoenn?.length > 0 ? data.kjoenn[0] : null
	const personSivilstand = data.sivilstand?.length > 0 ? data.sivilstand[0] : null
	const personFoedsel = data.foedsel?.length > 0 ? data.foedsel[0] : null
	const sikkerhetstiltak = data.sikkerhetstiltak?.length > 0 ? data.sikkerhetstiltak[0] : null

	return (
		<ErrorBoundary>
			<div>
				{visTittel && <SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />}
				<div className="person-visning_content">
					<TitleValue title="Fornavn" value={personNavn?.fornavn} />
					<TitleValue title="Mellomnavn" value={personNavn?.mellomnavn} />
					<TitleValue title="Etternavn" value={personNavn?.etternavn} />
					<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
					<TitleValue title="Sivilstand" value={personSivilstand?.type} />
					<TitleValue title="Fødselsdato" value={personFoedsel?.foedselsdato} />
					{sikkerhetstiltak && (
						<div className="person-visning_content">
							<h4 style={{ marginTop: '5px' }}>Sikkerhetstiltak</h4>
							<div className="person-visning_content">
								<TitleValue
									title="Type sikkerhetstiltak"
									value={`${sikkerhetstiltak.tiltakstype} - ${sikkerhetstiltak.beskrivelse}`}
								/>
								<TitleValue
									title="Sikkerhetstiltak starter"
									value={Formatters.formatDate(sikkerhetstiltak.gyldigFraOgMed)}
								/>
								<TitleValue
									title="Sikkerhetstiltak opphører"
									value={Formatters.formatDate(sikkerhetstiltak.gyldigTilOgMed)}
								/>
							</div>
						</div>
					)}
				</div>
				<UtenlandsId data={data.utenlandskIdentifikasjonsnummer} loading={false} />
				<FalskIdentitet data={data.falskIdentitet} loading={false} />
				<KontaktinformasjonForDoedsbo data={data.kontaktinformasjonForDoedsbo} loading={false} />
			</div>
		</ErrorBoundary>
	)
}
