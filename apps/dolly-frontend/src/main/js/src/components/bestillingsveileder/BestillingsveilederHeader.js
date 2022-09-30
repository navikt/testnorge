import React, { useContext } from 'react'
import { Header } from '~/components/ui/header/Header'
import Formatter from '~/utils/DataFormatter'
import { BestillingsveilederContext } from './Bestillingsveileder'
import { getLeggTilIdent } from '~/components/bestillingsveileder/utils'

const getImportFra = (opts) => {
	if (opts.is.leggTil) {
		if (opts.identMaster === 'PDL') {
			return 'Test-Norge'
		} else if (opts.personFoerLeggTil?.tpsf?.importFra) {
			return opts.personFoerLeggTil.tpsf.importFra
		}
	}
	return undefined
}

export const BestillingsveilederHeader = () => {
	const opts = useContext(BestillingsveilederContext)
	const ident = getLeggTilIdent(opts.personFoerLeggTil, opts.identMaster)
	const importFra = getImportFra(opts)

	if (opts.is.nyOrganisasjon || opts.is.nyStandardOrganisasjon || opts.is.nyOrganisasjonFraMal) {
		const titleValue = opts.is.nyStandardOrganisasjon ? 'Standard organisasjon' : 'Organisasjon'
		return (
			<Header icon="organisasjon" iconClassName="org">
				<div className="flexbox">
					<Header.TitleValue title="Opprett ny" value={titleValue} />
					{opts.is.nyOrganisasjonFraMal && (
						<Header.TitleValue title="Basert på mal" value={opts.mal.malNavn} />
					)}
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
				{opts.is.importTestnorge && <Header.TitleValue title="Importer fra" value="Test-Norge" />}
				{opts.is.leggTil && <Header.TitleValue title="Legg til/endre på person" value={ident} />}
				{importFra !== undefined && <Header.TitleValue title="Importert fra" value={importFra} />}
			</div>
		</Header>
	)
}
