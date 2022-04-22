import React, { useCallback, useEffect, useRef, useState } from 'react'
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
import { VergemaalVisning } from '~/components/fagsystem/pdlf/visning/partials/Vergemaal'
import { PdlDoedsfall } from '~/components/fagsystem/pdl/visning/partials/PdlDoedsfall'
import { TpsMessagingApi } from '~/service/Api'
import { getIdent } from '~/pages/testnorgePage/utils'
import { TpsMBankkonto } from '~/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMBankkonto'

type PdlVisningProps = {
	pdlData: PdlData
	loading?: boolean
	environments?: string[]
}

export const PdlVisning = ({ pdlData, loading = false, environments }: PdlVisningProps) => {
	if (loading) return <Loading label="Laster PDL-data" />
	if (!pdlData?.hentPerson) {
		return null
	}

	const [tpsMessagingData, setTpsMessagingData] = useState(null)
	const [tpsMessagingLoading, setTpsMessagingLoading] = useState(false)
	const mountedRef = useRef(true)

	const execute = useCallback(() => {
		const tpsMessaging = async () => {
			setTpsMessagingLoading(true)
			const resp = await TpsMessagingApi.getTpsPersonInfo(getIdent(pdlData), environments[0])
				.then((response: any) => {
					return response?.data[0]?.person
				})
				.catch((_e: Error) => {
					return null
				})
			if (mountedRef.current) {
				setTpsMessagingData(resp)
				setTpsMessagingLoading(false)
			}
		}
		return tpsMessaging()
	}, [environments])

	useEffect(() => {
		if (!loading && environments && environments.length > 0) {
			execute()
		}
		return () => {
			mountedRef.current = false
		}
	}, [])

	const { hentPerson, hentIdenter, hentGeografiskTilknytning } = pdlData
	const {
		foedsel,
		telefonnummer,
		vergemaalEllerFremtidsfullmakt,
		tilrettelagtKommunikasjon,
		bostedsadresse,
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
			<div className="boks">
				<PdlPersonInfo
					data={hentPerson}
					tpsMessagingData={tpsMessagingData}
					tpsMessagingLoading={tpsMessagingLoading}
				/>
				<IdentInfo pdlResponse={hentIdenter} />
				<GeografiskTilknytning data={hentGeografiskTilknytning} />
				<PdlNasjonalitet data={hentPerson} />
				<TpsMBankkonto data={tpsMessagingData} loading={tpsMessagingLoading} />
				<Foedsel data={foedsel} />
				<PdlDoedsfall data={doedsfall} />
				<Telefonnummer data={telefonnummer} />
				<VergemaalVisning data={vergemaalEllerFremtidsfullmakt} relasjoner={null} />
				<TilrettelagtKommunikasjon data={tilrettelagtKommunikasjon} />
				<PdlBoadresse data={bostedsadresse} />
				<PdlOppholdsadresse data={oppholdsadresse} />
				<PdlOppholdsstatus data={opphold} />
				<PdlKontaktadresse data={kontaktadresse} />
				<Adressebeskyttelse data={adressebeskyttelse} />
				<PdlFullmakt data={fullmakt} />
				<PdlSikkerhetstiltak data={sikkerhetstiltak} />
				<KontaktinformasjonForDoedsbo data={kontaktinformasjonForDoedsbo} relasjoner={null} />
				<PdlRelasjoner data={hentPerson} />
				<UtenlandsId data={utenlandskIdentifikasjonsnummer} />
				<FalskIdentitet data={falskIdentitet} />
			</div>
		</ErrorBoundary>
	)
}
