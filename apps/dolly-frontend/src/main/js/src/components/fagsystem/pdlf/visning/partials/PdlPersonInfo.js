import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { UtenlandsId } from '~/components/fagsystem/pdlf/visning/partials/UtenlandsId'
import { FalskIdentitet } from '~/components/fagsystem/pdlf/visning/partials/FalskIdentitet'
import Formatters from '~/utils/DataFormatter'

export const PdlPersonInfo = ({ data, visTittel = true }) => {
	if (!data) {
		return null
	}

	const personNavn = data?.navn?.[0]
	const personKjoenn = data?.kjoenn?.[0]
	const sikkerhetstiltak = data?.sikkerhetstiltak?.[0]
	const personstatus = data?.folkeregisterPersonstatus?.[0]

	return (
		<ErrorBoundary>
			<div>
				{visTittel && <SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />}
				<div className="person-visning_content">
					<TitleValue title="Ident" value={data?.ident} />
					<TitleValue title="Fornavn" value={personNavn?.fornavn} />
					<TitleValue title="Mellomnavn" value={personNavn?.mellomnavn} />
					<TitleValue title="Etternavn" value={personNavn?.etternavn} />
					<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
					<TitleValue title="Personstatus" value={personstatus?.status} />
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
			</div>
		</ErrorBoundary>
	)
}
