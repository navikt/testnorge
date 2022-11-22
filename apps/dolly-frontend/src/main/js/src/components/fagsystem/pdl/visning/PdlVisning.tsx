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
import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { TilrettelagtKommunikasjon } from '~/components/fagsystem/pdlf/visning/partials/TilrettelagtKommunikasjon'
import { PdlBoadresse } from '~/components/fagsystem/pdl/visning/partials/adresser/PdlBoadresse'
import { PdlOppholdsadresse } from '~/components/fagsystem/pdl/visning/partials/adresser/PdlOppholdsadresse'
import { PdlKontaktadresse } from '~/components/fagsystem/pdl/visning/partials/adresser/PdlKontaktadresse'
import { Adressebeskyttelse } from '~/components/fagsystem/pdlf/visning/partials/Adressebeskyttelse'
import { PdlRelasjoner } from '~/components/fagsystem/pdl/visning/partials/relasjoner/PdlRelasjoner'
import { UtenlandsId } from '~/components/fagsystem/pdlf/visning/partials/UtenlandsId'
import { FalskIdentitet } from '~/components/fagsystem/pdlf/visning/partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from '~/components/fagsystem/pdlf/visning/partials/KontaktinformasjonForDoedsbo'
import { PdlOppholdsstatus } from '~/components/fagsystem/pdlf/visning/partials/Oppholdsstatus'
import { Foedsel } from '~/components/fagsystem/pdlf/visning/partials/Foedsel'
import { getPdlIdent } from '~/pages/testnorgePage/utils'
import { TpsMBankkonto } from '~/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMBankkonto'
import { PdlDeltBosted } from '~/components/fagsystem/pdl/visning/partials/adresser/PdlDeltBosted'
import { Doedsfall } from '~/components/fagsystem/pdlf/visning/partials/Doedsfall'
import { TpsMessagingData } from '~/components/fagsystem/tpsmessaging/form/TpsMessagingData'
import { PdlVergemaal } from '~/components/fagsystem/pdl/visning/partials/vergemaal/PdlVergemaal'

type PdlVisningProps = {
	pdlData: PdlData
	loading?: boolean
	miljoeVisning?: boolean
}

export const PdlVisning = ({
	pdlData,
	loading = false,
	miljoeVisning = false,
}: PdlVisningProps) => {
	const tpsMessaging = TpsMessagingData(getPdlIdent(pdlData), loading)

	if (loading) {
		return <Loading label="Laster PDL-data" />
	}
	if (!pdlData?.hentPerson) {
		return null
	}

	const { hentPerson, hentIdenter, hentGeografiskTilknytning } = pdlData
	const {
		foedsel,
		telefonnummer,
		vergemaalEllerFremtidsfullmakt,
		tilrettelagtKommunikasjon,
		bostedsadresse,
		deltBosted,
		oppholdsadresse,
		opphold,
		kontaktadresse,
		adressebeskyttelse,
		fullmakt,
		utenlandskIdentifikasjonsnummer,
		falskIdentitet,
		sikkerhetstiltak,
		kontaktinformasjonForDoedsbo,
		doedsfall,
	} = hentPerson

	return (
		<ErrorBoundary>
			<div className={miljoeVisning ? 'boks' : ''}>
				<PdlPersonInfo
					data={hentPerson}
					tpsMessagingData={tpsMessaging?.tpsMessagingData}
					tpsMessagingLoading={tpsMessaging?.tpsMessagingLoading}
				/>
				<IdentInfo pdlResponse={hentIdenter} />
				<Foedsel data={foedsel} erPdlVisning />
				<Doedsfall data={doedsfall} erPdlVisning />
				<GeografiskTilknytning data={hentGeografiskTilknytning} />
				<PdlNasjonalitet data={hentPerson} />
				<Telefonnummer data={telefonnummer} erPdlVisning />
				<PdlVergemaal data={vergemaalEllerFremtidsfullmakt} />
				<PdlFullmakt data={fullmakt} />
				<PdlSikkerhetstiltak data={sikkerhetstiltak} />
				<TilrettelagtKommunikasjon data={tilrettelagtKommunikasjon} />
				<TpsMBankkonto
					data={tpsMessaging?.tpsMessagingData}
					loading={tpsMessaging?.tpsMessagingLoading}
				/>
				<PdlBoadresse data={bostedsadresse} />
				<PdlDeltBosted data={deltBosted} />
				<PdlOppholdsadresse data={oppholdsadresse} />
				<PdlOppholdsstatus data={opphold} />
				<PdlKontaktadresse data={kontaktadresse} />
				<Adressebeskyttelse data={adressebeskyttelse} erPdlVisning />
				<PdlRelasjoner data={hentPerson} />
				<FalskIdentitet data={falskIdentitet} />
				<UtenlandsId data={utenlandskIdentifikasjonsnummer} />
				<KontaktinformasjonForDoedsbo data={kontaktinformasjonForDoedsbo} relasjoner={null} />
			</div>
		</ErrorBoundary>
	)
}
