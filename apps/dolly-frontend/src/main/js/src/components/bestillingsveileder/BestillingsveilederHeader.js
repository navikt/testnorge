import React, { useContext } from 'react'
import { Header } from '~/components/ui/header/Header'
import Formatter from '~/utils/DataFormatter'
import { BestillingsveilederContext } from './Bestillingsveileder'
import { ImportFraEtikett } from '~/components/ui/etikett'

export const BestillingsveilederHeader = () => {
	const opts = useContext(BestillingsveilederContext)

	if (opts.is.nyOrganisasjon || opts.is.nyStandardOrganisasjon) {
		const titleValue = opts.is.nyStandardOrganisasjon ? 'Standard organisasjon' : 'Organisasjon'
		return (
			<Header icon="organisasjon" iconClassName="org">
				<div className="flexbox">
					<Header.TitleValue title="Opprett ny" value={titleValue} />
				</div>
			</Header>
		)
	}

	return (
		<Header>
			<div className="flexbox">
				<Header.TitleValue
					title="Antall"
					value={`${opts.antall} ${opts.antall > 1 ? 'personer' : 'person'}`}
				/>
				{!opts.is.opprettFraIdenter && (
					<Header.TitleValue title="Identtype" value={opts.identtype} />
				)}
				{opts.is.opprettFraIdenter && (
					<Header.TitleValue
						title="Opprett fra eksisterende personer"
						value={Formatter.arrayToString(opts.opprettFraIdenter)}
					/>
				)}
				{opts.is.nyBestillingFraMal && (
					<Header.TitleValue title="Basert på mal" value={opts.mal.malNavn} />
				)}
				{opts.is.leggTil && (
					<Header.TitleValue
						title="Legg til/endre på person"
						value={opts.personFoerLeggTil.tpsf.ident}
					/>
				)}
				{opts.is.leggTil && opts.personFoerLeggTil.tpsf.importFra && (
					<Header.TitleValue
						title="Importert fra"
						value={
							<ImportFraEtikett type="fokus" importFra={opts.personFoerLeggTil.tpsf.importFra} />
						}
					/>
				)}
			</div>
		</Header>
	)
}
