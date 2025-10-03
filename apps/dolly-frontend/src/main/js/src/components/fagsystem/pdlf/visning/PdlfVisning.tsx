import { UtenlandsId } from '@/components/fagsystem/pdlf/visning/partials/UtenlandsId'
import { FalskIdentitet } from '@/components/fagsystem/pdlf/visning/partials/FalskIdentitet'
import { KontaktinformasjonForDoedsbo } from '@/components/fagsystem/pdlf/visning/partials/KontaktinformasjonForDoedsbo'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Loading from '@/components/ui/loading/Loading'
import { Telefonnummer } from '@/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { TilrettelagtKommunikasjon } from '@/components/fagsystem/pdlf/visning/partials/TilrettelagtKommunikasjon'
import { Boadresse } from '@/components/fagsystem/pdlf/visning/partials/Boadresse'
import { Oppholdsadresse } from '@/components/fagsystem/pdlf/visning/partials/Oppholdsadresse'
import { Kontaktadresse } from '@/components/fagsystem/pdlf/visning/partials/Kontaktadresse'
import { Adressebeskyttelse } from '@/components/fagsystem/pdlf/visning/partials/Adressebeskyttelse'
import { Sivilstand } from '@/components/fagsystem/pdlf/visning/partials/Sivilstand'
import { ForelderBarnRelasjon } from '@/components/fagsystem/pdlf/visning/partials/ForelderBarnRelasjon'
import { DoedfoedtBarn } from '@/components/fagsystem/pdlf/visning/partials/DoedfoedtBarn'
import { Foedsel } from '@/components/fagsystem/pdlf/visning/partials/Foedsel'
import { Vergemaal } from '@/components/fagsystem/pdlf/visning/partials/Vergemaal'
import { IdenthistorikkVisning } from '@/components/fagsystem/pdlf/visning/partials/Identhistorikk'
import { DeltBosted } from '@/components/fagsystem/pdlf/visning/partials/DeltBosted'
import { Doedsfall } from '@/components/fagsystem/pdlf/visning/partials/Doedsfall'
import { Nasjonalitet } from '@/components/fagsystem/pdlf/visning/partials/Nasjonalitet'
import { Persondetaljer } from '@/components/fagsystem/pdlf/visning/partials/Persondetaljer'
import { PdlSikkerhetstiltak } from '@/components/fagsystem/pdl/visning/partials/PdlSikkerhetstiltak'
import { TpsMBankkonto } from '@/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMBankkonto'
import { ForeldreansvarVisning } from '@/components/fagsystem/pdlf/visning/partials/Foreldreansvar'
import FullmaktVisning from '@/components/fagsystem/fullmakt/visning/FullmaktVisning'

export const getBankkontoData = (data) => {
	if (data?.kontoregister) {
		return getKontoregisterBankkonto(data.kontoregister)
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
			gyldig: bankkontoData.gyldigFom,
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
			gyldig: bankkontoData?.gyldigFom,
		}
	}
	return resp
}

export const PdlfVisning = ({ fagsystemData, loading, tmpPersoner, erRedigerbar = true }) => {
	if (loading?.pdlforvalter) {
		return <Loading label="Laster PDL-data" />
	}

	const data = fagsystemData?.pdlforvalter
	if (!data) {
		return null
	}

	const ident = data?.person?.ident
	const tmpPdlforvalter = tmpPersoner?.pdlforvalter

	const bankkontoData = getBankkontoData(fagsystemData)

	const erDoed = data?.person?.doedsfall?.find((d) => d.doedsdato)

	return (
		<ErrorBoundary>
			<div>
				<Persondetaljer
					data={data?.person}
					tmpPersoner={tmpPersoner}
					ident={ident}
					tpsMessaging={fagsystemData?.tpsMessaging}
					tpsMessagingLoading={loading?.tpsMessaging}
					erRedigerbar={erRedigerbar}
				/>
				<Foedsel
					data={data?.person}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					erRedigerbar={erRedigerbar}
				/>
				<Doedsfall
					data={data?.person?.doedsfall}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					erRedigerbar={erRedigerbar}
				/>
				<Nasjonalitet
					data={data?.person}
					tmpPersoner={tmpPdlforvalter}
					identtype={data?.person?.identtype}
					erRedigerbar={erRedigerbar}
				/>
				<Telefonnummer
					data={data?.person?.telefonnummer}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					erRedigerbar={erRedigerbar}
				/>
				<Vergemaal
					data={data?.person?.vergemaal}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					relasjoner={data?.relasjoner}
					erRedigerbar={erRedigerbar}
				/>
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
					erRedigerbar={erRedigerbar}
				/>
				<DeltBosted
					data={data?.person?.deltBosted}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					personValues={data?.person}
					relasjoner={data?.relasjoner}
					erRedigerbar={erRedigerbar}
				/>
				<Oppholdsadresse
					data={data?.person?.oppholdsadresse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={data?.person?.identtype}
					erRedigerbar={erRedigerbar}
				/>
				<Kontaktadresse
					data={data?.person?.kontaktadresse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={data?.person?.identtype}
					erRedigerbar={erRedigerbar}
				/>
				<Adressebeskyttelse
					data={data?.person?.adressebeskyttelse}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={data?.person?.identtype}
					erPdlVisning={false}
					erRedigerbar={erRedigerbar}
				/>
				<Sivilstand
					data={data?.person?.sivilstand}
					relasjoner={data?.relasjoner}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={data?.person?.identtype}
					erRedigerbar={erRedigerbar}
				/>
				<ForelderBarnRelasjon
					data={data?.person?.forelderBarnRelasjon}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					relasjoner={data?.relasjoner}
					identtype={data?.person?.identtype}
					erRedigerbar={erRedigerbar}
				/>
				<ForeldreansvarVisning
					data={data?.person?.foreldreansvar}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					relasjoner={data?.relasjoner}
					personValues={data?.person}
					erRedigerbar={erRedigerbar}
				/>
				<DoedfoedtBarn
					data={data?.person?.doedfoedtBarn}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					erRedigerbar={erRedigerbar}
				/>
				{!erDoed && ident && <FullmaktVisning ident={ident} />}
				<FalskIdentitet data={data?.person?.falskIdentitet} />
				<UtenlandsId
					data={data?.person?.utenlandskIdentifikasjonsnummer}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					identtype={data?.person?.identtype}
					erRedigerbar={erRedigerbar}
				/>
				<IdenthistorikkVisning relasjoner={data?.relasjoner} />
				<KontaktinformasjonForDoedsbo
					data={data?.person?.kontaktinformasjonForDoedsbo}
					tmpPersoner={tmpPdlforvalter}
					ident={ident}
					relasjoner={data?.relasjoner}
					erRedigerbar={erRedigerbar}
				/>
			</div>
		</ErrorBoundary>
	)
}
