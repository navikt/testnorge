import { UtenlandsId } from '@/components/fagsystem/pdlf/visning/partials/UtenlandsId'
import { FalskIdentitet } from '@/components/fagsystem/pdlf/visning/partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from '@/components/fagsystem/pdlf/visning/partials/KontaktinformasjonForDoedsbo'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Loading from '@/components/ui/loading/Loading'
import { Fullmakt } from '@/components/fagsystem/pdlf/visning/partials/Fullmakt'
import { Telefonnummer } from '@/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { TilrettelagtKommunikasjon } from '@/components/fagsystem/pdlf/visning/partials/TilrettelagtKommunikasjon'
import { Boadresse } from '@/components/fagsystem/pdlf/visning/partials/Boadresse'
import { Oppholdsadresse } from '@/components/fagsystem/pdlf/visning/partials/Oppholdsadresse'
import { Kontaktadresse } from '@/components/fagsystem/pdlf/visning/partials/Kontaktadresse'
import { Adressebeskyttelse } from '@/components/fagsystem/pdlf/visning/partials/Adressebeskyttelse'
import { Sivilstand } from '@/components/fagsystem/pdlf/visning/partials/Sivilstand'
import { ForelderBarnRelasjonVisning } from '@/components/fagsystem/pdlf/visning/partials/ForeldreBarnRelasjon'
import { DoedfoedtBarnVisning } from '@/components/fagsystem/pdlf/visning/partials/DoedfoedtBarn'
import { Foedsel } from '@/components/fagsystem/pdlf/visning/partials/Foedsel'
import { Vergemaal } from '@/components/fagsystem/pdlf/visning/partials/Vergemaal'
import { IdenthistorikkVisning } from '@/components/fagsystem/pdlf/visning/partials/Identhistorikk'
import { DeltBosted } from '@/components/fagsystem/pdlf/visning/partials/DeltBosted'
import { Doedsfall } from '@/components/fagsystem/pdlf/visning/partials/Doedsfall'
import { Nasjonalitet } from '@/components/fagsystem/pdlf/visning/partials/Nasjonalitet'
import { Persondetaljer } from '@/components/fagsystem/pdlf/visning/partials/Persondetaljer'
import { PdlSikkerhetstiltak } from '@/components/fagsystem/pdl/visning/partials/PdlSikkerhetstiltak'
import { TpsMBankkonto } from '@/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMBankkonto'

export const getBankkontoData = (data) => {
	if (data?.kontoregister?.aktivKonto) {
		return getKontoregisterBankkonto(data.kontoregister.aktivKonto)
	} else {
		return {
			bankkontonrUtland: data?.tpsMessaging?.bankkontonrUtland,
			bankkontonrNorsk: data?.tpsMessaging?.bankkontonrNorsk,
		}
	}
}

const getKontoregisterBankkonto = (bankkontoData) => {
	const resp = {
		bankkontonrUtland: null,
		bankkontonrNorsk: null,
	}
	if (bankkontoData.utenlandskKontoInfo) {
		resp.bankkontonrUtland = {
			kontonummer: bankkontoData.kontonummer,
			swift: bankkontoData.utenlandskKontoInfo?.swiftBicKode,
			landkode: bankkontoData.utenlandskKontoInfo?.bankLandkode,
			banknavn: bankkontoData.utenlandskKontoInfo?.banknavn,
			iban: bankkontoData.kontonummer,
			valuta: bankkontoData.utenlandskKontoInfo?.valutakode,
			bankAdresse1: bankkontoData.utenlandskKontoInfo?.bankadresse1,
			bankAdresse2: bankkontoData.utenlandskKontoInfo?.bankadresse2,
			bankAdresse3: bankkontoData.utenlandskKontoInfo?.bankadresse3,
		}
	} else {
		resp.bankkontonrNorsk = {
			kontonummer: bankkontoData?.kontonummer,
		}
	}
	return resp
}

export const PdlfVisning = ({ fagsystemData, loading, tmpPersoner }) => {
	if (loading?.pdlforvalter) {
		return <Loading label="Laster PDL-data" />
	}

	const data = fagsystemData?.pdlforvalter
	if (!data) {
		return null
	}

	const ident = data?.person?.ident
	const tmpPdlforvalter = tmpPersoner?.pdlforvalter
	const skjermingData = fagsystemData?.skjermingsregister

	const bankkontoData = getBankkontoData(fagsystemData)

	return (
		<ErrorBoundary>
			<div>
				<Persondetaljer
					data={data?.person}
					tmpPersoner={tmpPersoner}
					ident={ident}
					tpsMessaging={fagsystemData?.tpsMessaging}
					tpsMessagingLoading={loading?.tpsMessaging}
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
				<TpsMBankkonto
					data={bankkontoData}
					loading={loading?.tpsMessaging || loading?.kontoregister}
					ident={ident}
					extraButtons={true}
				/>
				<Boadresse
					data={data?.person?.bostedsadresse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={data?.person?.identtype}
				/>
				<DeltBosted data={data?.person?.deltBosted} />
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
				<Sivilstand
					data={data?.person?.sivilstand}
					relasjoner={data?.relasjoner}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
				/>
				<ForelderBarnRelasjonVisning
					data={data?.person?.forelderBarnRelasjon}
					relasjoner={data?.relasjoner}
				/>
				<DoedfoedtBarnVisning data={data?.person?.doedfoedtBarn} />
				<FalskIdentitet data={data?.person?.falskIdentitet} />
				<UtenlandsId data={data?.person?.utenlandskIdentifikasjonsnummer} />
				<IdenthistorikkVisning relasjoner={data?.relasjoner} />
				<KontaktinformasjonForDoedsbo
					data={data?.person?.kontaktinformasjonForDoedsbo}
					relasjoner={data?.relasjoner}
				/>
			</div>
		</ErrorBoundary>
	)
}
