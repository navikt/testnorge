import React from 'react'
import { UtenlandsId } from './partials/UtenlandsId'
import { FalskIdentitet } from './partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from './partials/KontaktinformasjonForDoedsbo'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Loading from '~/components/ui/loading/Loading'
import { Fullmakt } from '~/components/fagsystem/pdlf/visning/partials/Fullmakt'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { TilrettelagtKommunikasjon } from '~/components/fagsystem/pdlf/visning/partials/TilrettelagtKommunikasjon'
import { Boadresse } from '~/components/fagsystem/pdlf/visning/partials/Boadresse'
import { Oppholdsadresse } from '~/components/fagsystem/pdlf/visning/partials/Oppholdsadresse'
import { Kontaktadresse } from '~/components/fagsystem/pdlf/visning/partials/Kontaktadresse'
import { Adressebeskyttelse } from '~/components/fagsystem/pdlf/visning/partials/Adressebeskyttelse'
import { SivilstandVisning } from '~/components/fagsystem/pdlf/visning/partials/Sivilstand'
import { ForelderBarnRelasjonVisning } from '~/components/fagsystem/pdlf/visning/partials/ForeldreBarnRelasjon'
import { DoedfoedtBarnVisning } from '~/components/fagsystem/pdlf/visning/partials/DoedfoedtBarn'
import { Foedsel } from '~/components/fagsystem/pdlf/visning/partials/Foedsel'
import { Vergemaal } from '~/components/fagsystem/pdlf/visning/partials/Vergemaal'
import { IdenthistorikkVisning } from '~/components/fagsystem/pdlf/visning/partials/Identhistorikk'
import { DeltBosted } from '~/components/fagsystem/pdlf/visning/partials/DeltBosted'
import { Doedsfall } from '~/components/fagsystem/pdlf/visning/partials/Doedsfall'
import { Nasjonalitet } from '~/components/fagsystem/pdlf/visning/partials/Nasjonalitet'
import { Persondetaljer } from '~/components/fagsystem/pdlf/visning/partials/Persondetaljer'
import {
	MidlertidigAdresse,
	Postadresse,
	Relasjoner,
	TpsfBoadresse,
	TpsfIdenthistorikk,
	TpsfNasjonalitet,
	TpsfPersoninfo,
	TpsfVergemaal,
} from '~/components/fagsystem/tpsf/visning/partials'
import { PdlSikkerhetstiltak } from '~/components/fagsystem/pdl/visning/partials/PdlSikkerhetstiltak'
import { TpsMessagingData } from '~/components/fagsystem/tpsmessaging/form/TpsMessagingData'
import { TpsMBankkonto } from '~/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMBankkonto'

export const PdlfVisning = ({ data, tpsfData, skjermingData, loading, tmpPersoner, master }) => {
	if (loading) {
		return <Loading label="Laster PDL-data" />
	}
	if (!data && !tpsfData) {
		return null
	}

	const ident = data ? data.person?.ident : tpsfData?.ident
	const tpsMessaging = TpsMessagingData(ident)
	const tmpPdlforvalter = tmpPersoner?.pdlforvalter

	const getBankkontoData = () => {
		return {
			bankkontonrUtland: tpsMessaging?.tpsMessagingData?.bankkontonrUtland
				? tpsMessaging?.tpsMessagingData?.bankkontonrUtland
				: tpsfData?.bankkontonrUtland,
			bankkontonrNorsk: tpsMessaging?.tpsMessagingData?.bankkontonrNorsk
				? tpsMessaging?.tpsMessagingData?.bankkontonrNorsk
				: tpsfData?.bankkontonrNorsk,
		}
	}

	return (
		<ErrorBoundary>
			<div>
				{master === 'PDLF' ? (
					<>
						<Persondetaljer
							data={data?.person}
							tmpPersoner={tmpPersoner}
							ident={ident}
							tpsMessaging={tpsMessaging}
							skjermingData={skjermingData}
						/>
						<Foedsel data={data?.person?.foedsel} tmpPersoner={tmpPdlforvalter} ident={ident} />
						<Doedsfall data={data?.person?.doedsfall} tmpPersoner={tmpPdlforvalter} ident={ident} />
						<Nasjonalitet data={data?.person} tmpPersoner={tmpPdlforvalter} />
						<Telefonnummer
							data={data?.person?.telefonnummer}
							tmpPersoner={tmpPdlforvalter}
							ident={ident}
						/>
						<Vergemaal
							data={data?.person?.vergemaal}
							tmpPersoner={tmpPdlforvalter}
							ident={ident}
							relasjoner={data?.relasjoner}
						/>
						<Fullmakt data={data?.person?.fullmakt} relasjoner={data?.relasjoner} />
						<PdlSikkerhetstiltak data={data?.person?.sikkerhetstiltak} />
						<TilrettelagtKommunikasjon data={data?.person?.tilrettelagtKommunikasjon} />
					</>
				) : (
					<>
						<TpsfPersoninfo data={tpsfData} />
						<Doedsfall data={data?.person?.doedsfall} tmpPersoner={tmpPdlforvalter} ident={ident} />
						<TpsfNasjonalitet data={tpsfData} />
						<Telefonnummer data={tpsfData?.telefonnumre} />
						<TpsfVergemaal data={tpsfData?.vergemaal} />
						<Fullmakt data={tpsfData?.fullmakt} relasjoner={tpsfData?.relasjoner} />
					</>
				)}
				<TpsMBankkonto
					data={getBankkontoData()}
					loading={tpsMessaging?.tpsMessagingLoading}
					ident={ident}
					extraButtons={true}
				/>
				<Boadresse
					data={data?.person?.bostedsadresse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={data?.person?.identtype}
				/>
				{!data?.person?.bostedsadresse && <TpsfBoadresse boadresse={tpsfData?.boadresse} />}
				<DeltBosted data={data?.person?.deltBosted} />
				<Postadresse postadresse={tpsfData?.postadresse} />
				<MidlertidigAdresse midlertidigAdresse={tpsfData?.midlertidigAdresse} />
				<Oppholdsadresse
					data={data?.person?.oppholdsadresse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
				/>
				<Kontaktadresse
					data={data?.person?.kontaktadresse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
				/>
				<Adressebeskyttelse
					data={data?.person?.adressebeskyttelse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={data?.person?.identtype}
				/>

				{master === 'PDLF' ? (
					<>
						<SivilstandVisning data={data?.person?.sivilstand} relasjoner={data?.relasjoner} />
						<ForelderBarnRelasjonVisning
							data={data?.person?.forelderBarnRelasjon}
							relasjoner={data?.relasjoner}
						/>
						<DoedfoedtBarnVisning data={data?.person?.doedfoedtBarn} />
					</>
				) : (
					<Relasjoner relasjoner={tpsfData?.relasjoner} />
				)}

				{master === 'PDLF' ? (
					<>
						<FalskIdentitet data={data?.person?.falskIdentitet} />
						<UtenlandsId data={data?.person?.utenlandskIdentifikasjonsnummer} />
						<IdenthistorikkVisning relasjoner={data?.relasjoner} />
					</>
				) : (
					<TpsfIdenthistorikk identhistorikk={tpsfData?.identHistorikk} />
				)}

				<KontaktinformasjonForDoedsbo
					data={data?.person?.kontaktinformasjonForDoedsbo}
					relasjoner={data?.relasjoner}
				/>
			</div>
		</ErrorBoundary>
	)
}
