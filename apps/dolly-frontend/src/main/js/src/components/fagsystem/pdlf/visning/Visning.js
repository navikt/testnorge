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
import { VergemaalVisning } from '~/components/fagsystem/pdlf/visning/partials/Vergemaal'
import { IdenthistorikkVisning } from '~/components/fagsystem/pdlf/visning/partials/Identhistorikk'
import { DeltBosted } from '~/components/fagsystem/pdlf/visning/partials/DeltBosted'
import { Doedsfall } from '~/components/fagsystem/pdlf/visning/partials/Doedsfall'
import { Nasjonalitet } from '~/components/fagsystem/pdlf/visning/partials/Nasjonalitet'

export const PdlfVisning = ({ data, loading, tmpPersoner }) => {
	if (loading) return <Loading label="Laster PDL-data" />
	if (!data) return null

	const {
		foedsel,
		doedsfall,
		telefonnummer,
		vergemaal,
		tilrettelagtKommunikasjon,
		bostedsadresse,
		deltBosted,
		oppholdsadresse,
		kontaktadresse,
		adressebeskyttelse,
		sivilstand,
		fullmakt,
		utenlandskIdentifikasjonsnummer,
		falskIdentitet,
		kontaktinformasjonForDoedsbo,
		forelderBarnRelasjon,
		doedfoedtBarn,
		ident,
	} = data.person

	return (
		<ErrorBoundary>
			<div>
				<Nasjonalitet data={data.person} tmpPersoner={tmpPersoner} />
				<Foedsel data={foedsel} tmpPersoner={tmpPersoner} ident={ident} />
				<Doedsfall data={doedsfall} tmpPersoner={tmpPersoner} ident={ident} />
				<Telefonnummer data={telefonnummer} />
				<VergemaalVisning data={vergemaal} relasjoner={data.relasjoner} />
				<TilrettelagtKommunikasjon data={tilrettelagtKommunikasjon} />
				<Boadresse data={bostedsadresse} />
				<DeltBosted data={deltBosted} />
				<Oppholdsadresse data={oppholdsadresse} />
				<Kontaktadresse data={kontaktadresse} />
				<Adressebeskyttelse data={adressebeskyttelse} />
				<SivilstandVisning data={sivilstand} relasjoner={data.relasjoner} />
				<DoedfoedtBarnVisning data={doedfoedtBarn} />
				<ForelderBarnRelasjonVisning data={forelderBarnRelasjon} relasjoner={data.relasjoner} />
				<Fullmakt data={fullmakt} relasjoner={data.relasjoner} />
				<UtenlandsId data={utenlandskIdentifikasjonsnummer} />
				<FalskIdentitet data={falskIdentitet} />
				<IdenthistorikkVisning relasjoner={data.relasjoner} />
				<KontaktinformasjonForDoedsbo
					data={kontaktinformasjonForDoedsbo}
					relasjoner={data.relasjoner}
				/>
			</div>
		</ErrorBoundary>
	)
}
