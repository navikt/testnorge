import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Loading from '@/components/ui/loading/Loading'
import { Telefonnummer } from '@/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { PdlPersonInfo } from '@/components/fagsystem/pdl/visning/partials/PdlPersonInfo'
import { IdentInfo } from '@/components/fagsystem/pdlf/visning/partials/Identinfo'
import { GeografiskTilknytning } from '@/components/fagsystem/pdlf/visning/partials/GeografiskTilknytning'
import { PdlNasjonalitet } from '@/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlNasjonalitet'
import { PdlSikkerhetstiltak } from '@/components/fagsystem/pdl/visning/partials/PdlSikkerhetstiltak'
import { PdlData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { TilrettelagtKommunikasjon } from '@/components/fagsystem/pdlf/visning/partials/TilrettelagtKommunikasjon'
import { PdlBoadresse } from '@/components/fagsystem/pdl/visning/partials/adresser/PdlBoadresse'
import { PdlOppholdsadresse } from '@/components/fagsystem/pdl/visning/partials/adresser/PdlOppholdsadresse'
import { PdlKontaktadresse } from '@/components/fagsystem/pdl/visning/partials/adresser/PdlKontaktadresse'
import { Adressebeskyttelse } from '@/components/fagsystem/pdlf/visning/partials/Adressebeskyttelse'
import { PdlRelasjoner } from '@/components/fagsystem/pdl/visning/partials/relasjoner/PdlRelasjoner'
import { UtenlandsId } from '@/components/fagsystem/pdlf/visning/partials/UtenlandsId'
import { FalskIdentitet } from '@/components/fagsystem/pdlf/visning/partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from '@/components/fagsystem/pdlf/visning/partials/KontaktinformasjonForDoedsbo'
import { PdlOppholdsstatus } from '@/components/fagsystem/pdlf/visning/partials/Oppholdsstatus'
import { Foedsel } from '@/components/fagsystem/pdlf/visning/partials/Foedsel'
import { TpsMBankkonto } from '@/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMBankkonto'
import { PdlDeltBosted } from '@/components/fagsystem/pdl/visning/partials/adresser/PdlDeltBosted'
import { Doedsfall } from '@/components/fagsystem/pdlf/visning/partials/Doedsfall'
import { PdlVergemaal } from '@/components/fagsystem/pdl/visning/partials/vergemaal/PdlVergemaal'
import { getBankkontoData } from '@/components/fagsystem/pdlf/visning/PdlfVisning'
import React from 'react'
import FullmaktVisning from '@/components/fagsystem/fullmakt/visning/FullmaktVisning'

type PdlVisningProps = {
	pdlData: PdlData
	tmpPersoner: any
	fagsystemData?: any
	loading?: any
	miljoeVisning?: boolean
}

export const PdlVisning = ({
	pdlData,
	tmpPersoner,
	fagsystemData = {},
	loading = {},
	miljoeVisning = false,
}: PdlVisningProps) => {
	if (loading?.pdl || (miljoeVisning && loading)) {
		return <Loading label="Laster PDL-data" />
	}
	if (!pdlData?.hentPerson) {
		return null
	}

	const { hentPerson, hentIdenter, hentGeografiskTilknytning, ident } = pdlData
	const {
		telefonnummer,
		vergemaalEllerFremtidsfullmakt,
		tilrettelagtKommunikasjon,
		bostedsadresse,
		deltBosted,
		oppholdsadresse,
		opphold,
		kontaktadresse,
		adressebeskyttelse,
		utenlandskIdentifikasjonsnummer,
		falskIdentitet,
		sikkerhetstiltak,
		kontaktinformasjonForDoedsbo,
		doedsfall,
	} = hentPerson

	const bankkontoData = getBankkontoData(fagsystemData)

	const pdlfPerson = fagsystemData?.pdlforvalter?.person
	const identtype = pdlfPerson?.identtype
	const tmpPdlforvalter = tmpPersoner?.pdlforvalter

	const gjeldendeIdent = hentIdenter?.identer?.find(
		(id) => !id.historisk && id.gruppe === 'FOLKEREGISTERIDENT',
	)

	const erDoed = doedsfall?.find((d) => d.doedsdato)

	return (
		<ErrorBoundary>
			<div className={miljoeVisning ? 'boks' : ''}>
				<PdlPersonInfo
					data={hentPerson}
					tpsMessagingData={fagsystemData?.tpsMessaging}
					tpsMessagingLoading={loading?.tpsMessaging}
				/>
				<IdentInfo pdlResponse={hentIdenter} />
				<Foedsel data={hentPerson} erPdlVisning />
				<Doedsfall data={doedsfall} erPdlVisning />
				<GeografiskTilknytning data={hentGeografiskTilknytning} />
				<PdlNasjonalitet data={hentPerson} />
				<Telefonnummer data={telefonnummer} erPdlVisning />
				<PdlVergemaal data={vergemaalEllerFremtidsfullmakt} />
				<PdlSikkerhetstiltak data={sikkerhetstiltak} />
				<TilrettelagtKommunikasjon data={tilrettelagtKommunikasjon} />
				<TpsMBankkonto
					data={bankkontoData}
					loading={loading?.tpsMessaging || loading?.kontoregister}
				/>
				<PdlBoadresse
					data={bostedsadresse}
					pdlfData={pdlfPerson?.bostedsadresse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={identtype}
				/>
				<PdlDeltBosted data={deltBosted} />
				<PdlOppholdsadresse
					data={oppholdsadresse}
					pdlfData={pdlfPerson?.oppholdsadresse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={identtype}
				/>
				<PdlOppholdsstatus data={opphold} />
				<PdlKontaktadresse
					data={kontaktadresse}
					pdlfData={pdlfPerson?.kontaktadresse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={identtype}
				/>
				<Adressebeskyttelse
					data={adressebeskyttelse}
					pdlfData={pdlfPerson?.adressebeskyttelse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={identtype}
					erPdlVisning={miljoeVisning}
				/>
				<PdlRelasjoner data={hentPerson} />
				{!erDoed && gjeldendeIdent && <FullmaktVisning ident={gjeldendeIdent?.ident} />}
				<FalskIdentitet data={falskIdentitet} />
				<UtenlandsId data={utenlandskIdentifikasjonsnummer} />
				<KontaktinformasjonForDoedsbo
					data={kontaktinformasjonForDoedsbo}
					relasjoner={null}
					erPdlVisning
				/>
			</div>
		</ErrorBoundary>
	)
}
