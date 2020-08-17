import React, { useContext } from 'react'
import Etikett from 'nav-frontend-etiketter'
import { Header } from '~/components/ui/header/Header'
import Formatter from '~/utils/DataFormatter'
import { BestillingsveilederContext } from './Bestillingsveileder'
import { ImportFraEtikett } from '~/components/ui/etikett'

export const BestillingsveilederHeader = () => {
	const opts = useContext(BestillingsveilederContext)
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
					<Header.TitleValue title="Legg til på person" value={opts.personFoerLeggTil.tpsf.ident} />
				)}
				{opts.is.leggTil && opts.personFoerLeggTil.tpsf.importFra && (
					<Header.TitleValue
						title="Import fra"
						value={
							<ImportFraEtikett type="fokus" importFra={opts.personFoerLeggTil.tpsf.importFra} />
						}
					/>
				)}
			</div>
		</Header>
	)
}
