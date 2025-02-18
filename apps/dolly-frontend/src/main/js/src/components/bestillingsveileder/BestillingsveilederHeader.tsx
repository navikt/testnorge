import React, { useContext } from 'react'
import { Header } from '@/components/ui/header/Header'
import { arrayToString } from '@/utils/DataFormatter'
import { getLeggTilIdent } from '@/components/bestillingsveileder/utils'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'

export const BestillingsveilederHeader = () => {
	const formMethods = useFormContext()
	const opts = useContext(BestillingsveilederContext)
	const ident = getLeggTilIdent(opts.personFoerLeggTil, opts.identMaster)

	const importFra = opts.is.leggTil && opts.identMaster === 'PDL' ? 'Test-Norge' : undefined
	const formGruppeId = formMethods.watch('gruppeId')

	const { gruppe } = useGruppeById(opts?.gruppeId || opts?.gruppe?.id || formGruppeId)

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
				{!opts.is.opprettFraIdenter && !opts.is.leggTilPaaGruppe && (
					<Header.TitleValue title="Identtype" value={opts.identtype} />
				)}
				{opts.is.opprettFraIdenter && (
					<Header.TitleValue
						title="Opprett fra eksisterende personer"
						value={arrayToString(opts.opprettFraIdenter)}
					/>
				)}
				{opts.is.nyBestillingFraMal && (
					<Header.TitleValue title="Basert på mal" value={opts.mal.malNavn} />
				)}
				{opts.is.importTestnorge && <Header.TitleValue title="Importer fra" value="Test-Norge" />}
				{opts.is.leggTil && <Header.TitleValue title="Legg til/endre på person" value={ident} />}
				{opts.is.leggTilPaaGruppe && (
					<Header.TitleValue
						title="Legg til på / endre alle personer"
						value={`Gruppe #${formGruppeId || opts?.gruppeId} - ${gruppe?.navn}`}
					/>
				)}
				{importFra !== undefined && <Header.TitleValue title="Importert fra" value={importFra} />}
				{!opts.is.leggTilPaaGruppe && <Header.TitleValue title="Gruppe" value={gruppe?.navn} />}
			</div>
		</Header>
	)
}
