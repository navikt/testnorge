import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Loading from '~/components/ui/loading/Loading'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { PdlPersonInfo } from '~/components/fagsystem/pdl/visning/partials/PdlPersonInfo'
import { IdentInfo } from '~/components/fagsystem/pdlf/visning/partials/Identinfo'
import { GeografiskTilknytning } from '~/components/fagsystem/pdlf/visning/partials/GeografiskTilknytning'
import { PdlNasjonalitet } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlNasjonalitet'
import { PdlFullmakt } from '~/components/fagsystem/pdl/visning/partials/PdlFullmakt'
import { PdlSikkerhetstiltak } from '~/components/fagsystem/pdl/visning/partials/PdlSikkerhetstiltak'
import { PdlDataWrapper } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { TilrettelagtKommunikasjon } from '~/components/fagsystem/pdlf/visning/partials/TilrettelagtKommunikasjon'
import { PdlBoadresse } from '~/components/fagsystem/pdl/visning/partials/adresser/PdlBoadresse'
import { PdlOppholdsadresse } from '~/components/fagsystem/pdl/visning/partials/adresser/PdlOppholdsadresse'
import { PdlKontaktadresse } from '~/components/fagsystem/pdl/visning/partials/adresser/PdlKontaktadresse'
import { Adressebeskyttelse } from '~/components/fagsystem/pdlf/visning/partials/Adressebeskyttelse'
import { PdlRelasjoner } from '~/components/fagsystem/pdl/visning/partials/relasjoner/PdlRelasjoner'
import { UtenlandsId } from '~/components/fagsystem/pdlf/visning/partials/UtenlandsId'
import { FalskIdentitet } from '~/components/fagsystem/pdlf/visning/partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from '~/components/fagsystem/pdlf/visning/partials/KontaktinformasjonForDoedsbo'

type PdlVisningProps = {
	pdlData: PdlDataWrapper
	loading?: boolean
}

export const PdlVisning = ({ pdlData, loading }: PdlVisningProps) => {
	if (loading) return <Loading label="Laster PDL-data" />

	const data = pdlData.data
	if (!data || !data.hentPerson) {
		return null
	}
	const { hentPerson, hentIdenter, hentGeografiskTilknytning } = data
	const {
		telefonnummer,
		tilrettelagtKommunikasjon,
		bostedsadresse,
		oppholdsadresse,
		kontaktadresse,
		adressebeskyttelse,
		fullmakt,
		utenlandskIdentifikasjonsnummer,
		falskIdentitet,
		sikkerhetstiltak,
		kontaktinformasjonForDoedsbo,
	} = hentPerson

	return (
		<ErrorBoundary>
			<div className="boks">
				<PdlPersonInfo data={hentPerson} />
				<IdentInfo pdlResponse={hentIdenter} />
				<GeografiskTilknytning data={hentGeografiskTilknytning} />
				<PdlNasjonalitet data={hentPerson} />
				<Telefonnummer data={telefonnummer} />
				<TilrettelagtKommunikasjon data={tilrettelagtKommunikasjon} />
				<PdlBoadresse data={bostedsadresse} />
				<PdlOppholdsadresse data={oppholdsadresse} />
				<PdlKontaktadresse data={kontaktadresse} />
				<Adressebeskyttelse data={adressebeskyttelse} />
				<PdlFullmakt data={fullmakt} />
				<PdlSikkerhetstiltak data={sikkerhetstiltak} />
				<PdlRelasjoner data={hentPerson} />
				<UtenlandsId data={utenlandskIdentifikasjonsnummer} />
				<FalskIdentitet data={falskIdentitet} />
				<KontaktinformasjonForDoedsbo data={kontaktinformasjonForDoedsbo} />
			</div>
		</ErrorBoundary>
	)
}
