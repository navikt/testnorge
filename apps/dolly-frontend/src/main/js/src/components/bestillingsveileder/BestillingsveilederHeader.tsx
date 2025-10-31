import React from 'react'
import { Header } from '@/components/ui/header/Header'
import { arrayToString } from '@/utils/DataFormatter'
import { getLeggTilIdent } from '@/components/bestillingsveileder/utils'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useWatch } from 'react-hook-form'
import { BestillingsveilederContextType } from '@/components/bestillingsveileder/BestillingsveilederContext'

export const BestillingsveilederHeader = ({
	context,
	formMethods,
}: {
	context: BestillingsveilederContextType
	formMethods: UseFormReturn
}) => {
	const formGruppeId = useWatch({ control: formMethods.control, name: 'gruppeId' }) || ''
	const malWatchValue = useWatch({ control: formMethods.control, name: 'mal' }) || ''
	const antallWatch = useWatch({ control: formMethods.control, name: 'antall' })
	const identtypeWatch = useWatch({
		control: formMethods.control,
		name: 'pdldata.opprettNyPerson.identtype',
	})
	const id2032Watch = useWatch({
		control: formMethods.control,
		name: 'pdldata.opprettNyPerson.id2032',
	})
	const opprettFraIdenterWatch = useWatch({
		control: formMethods.control,
		name: 'opprettFraIdenter',
	})

	const ctx = { ...context }
	const {
		is,
		gruppeId,
		gruppe,
		personFoerLeggTil,
		identMaster,
		identtype,
		id2032,
		mal,
		antall,
		opprettFraIdenter,
	} = ctx

	const valgtGruppeId = formGruppeId || gruppeId || gruppe?.id
	const { gruppe: valgtGruppe } = useGruppeById(valgtGruppeId)

	const ident = getLeggTilIdent(personFoerLeggTil, identMaster) || ''
	const importFra = is?.leggTil && identMaster === 'PDL' ? 'Test-Norge' : ''

	const effectiveAntall = antallWatch ?? antall
	const effectiveIdenttype = identtypeWatch ?? identtype
	const effectiveId2032 = id2032Watch ?? id2032
	const effectiveOpprettFraIdenter = opprettFraIdenterWatch ?? opprettFraIdenter

	const identtypeValue = effectiveIdenttype
		? effectiveId2032
			? `${effectiveIdenttype} (id 2032)`
			: effectiveIdenttype
		: ''

	const malNavn = mal?.malNavn || ''
	const showMalNavn = !!malNavn && !!malWatchValue

	if (is?.nyOrganisasjon || is?.nyStandardOrganisasjon || is?.nyOrganisasjonFraMal) {
		const titleValue = is?.nyStandardOrganisasjon ? 'Standard organisasjon' : 'Organisasjon'
		return (
			<Header icon="organisasjon" iconClassName="org">
				<div className="flexbox">
					<Header.TitleValue title="Opprett ny" value={titleValue} />
					{showMalNavn && <Header.TitleValue title="Basert på mal" value={malNavn} />}
				</div>
			</Header>
		)
	}

	return (
		<Header>
			<div className="flexbox">
				<Header.TitleValue
					title="Antall"
					value={`${effectiveAntall ?? 0} ${effectiveAntall && effectiveAntall > 1 ? 'personer' : 'person'}`}
				/>
				{!is?.opprettFraIdenter && !is?.leggTilPaaGruppe && (
					<Header.TitleValue title="Identtype" value={identtypeValue} />
				)}
				{is?.opprettFraIdenter && (
					<Header.TitleValue
						title="Opprett fra eksisterende personer"
						value={arrayToString(effectiveOpprettFraIdenter) || ''}
					/>
				)}
				{showMalNavn && <Header.TitleValue title="Basert på mal" value={malNavn} />}
				{is?.importTestnorge && <Header.TitleValue title="Importer fra" value="Test-Norge" />}
				{is?.leggTil && <Header.TitleValue title="Legg til/endre på person" value={ident} />}
				{is?.leggTilPaaGruppe && (
					<Header.TitleValue
						title="Legg til på / endre alle personer"
						value={`Gruppe #${valgtGruppeId || ''} - ${valgtGruppe?.navn || ''}`}
					/>
				)}
				{importFra && <Header.TitleValue title="Importert fra" value={importFra} />}
				{!is?.leggTilPaaGruppe && (
					<Header.TitleValue title="Gruppe" value={valgtGruppe?.navn || ''} />
				)}
			</div>
		</Header>
	)
}
