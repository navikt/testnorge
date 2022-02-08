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
import { PdlOppholdsstatus } from '~/components/fagsystem/pdlf/visning/partials/Oppholdsstatus'

export const PdlfVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster PDL-data" />
	if (!data) return null

	const {
		foedsel,
		telefonnummer,
		vergemaal,
		tilrettelagtKommunikasjon,
		bostedsadresse,
		oppholdsadresse,
		kontaktadresse,
		adressebeskyttelse,
		sivilstand,
		fullmakt,
		utenlandskIdentifikasjonsnummer,
		falskIdentitet,
		kontaktinformasjonForDoedsbo,
		forelderBarnRelasjon,
		opphold,
		doedfoedtBarn,
	} = data.person

	return (
		<ErrorBoundary>
			<div>
				<PdlOppholdsstatus data={opphold} />
				<Foedsel data={foedsel} />
				<Telefonnummer data={telefonnummer} />
				<VergemaalVisning data={vergemaal} relasjoner={data.relasjoner} />
				<TilrettelagtKommunikasjon data={tilrettelagtKommunikasjon} />
				<Boadresse data={bostedsadresse} />
				<Oppholdsadresse data={oppholdsadresse} />
				<Kontaktadresse data={kontaktadresse} />
				<Adressebeskyttelse data={adressebeskyttelse} />
				<SivilstandVisning data={sivilstand} relasjoner={data.relasjoner} />
				<DoedfoedtBarnVisning data={doedfoedtBarn} />
				<ForelderBarnRelasjonVisning data={forelderBarnRelasjon} relasjoner={data.relasjoner} />
				<Fullmakt data={fullmakt} relasjoner={data.relasjoner} />
				<UtenlandsId data={utenlandskIdentifikasjonsnummer} />
				<FalskIdentitet data={falskIdentitet} />
				<KontaktinformasjonForDoedsbo
					data={kontaktinformasjonForDoedsbo}
					relasjoner={data.relasjoner}
				/>
			</div>
		</ErrorBoundary>
	)
}
