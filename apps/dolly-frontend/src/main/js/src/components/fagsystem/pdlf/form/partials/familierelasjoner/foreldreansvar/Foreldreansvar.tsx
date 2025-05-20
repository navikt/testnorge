import * as React from 'react'
import { useContext, useEffect } from 'react'
import {
	getInitialForeldreansvar,
	initialPdlBiPerson,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as _ from 'lodash-es'
import { ForeldreBarnRelasjon, TypeAnsvarlig } from '@/components/fagsystem/pdlf/PdlTypes'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { PdlNyPerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { PdlPersonUtenIdentifikator } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonUtenIdentifikator'
import { Alert } from '@navikt/ds-react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import styled from 'styled-components'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface ForeldreansvarForm {
	formMethods: UseFormReturn
	path: string
	eksisterendeNyPerson?: any
}

type Target = {
	label: string
	value: string
}

const StyledAlert = styled(Alert)`
	margin-bottom: 20px;

	&&& {
		.navds-alert__wrapper {
			max-width: 60rem;
		}
	}
`

export const ForeldreansvarForm = ({
	formMethods,
	path,
	eksisterendeNyPerson = null,
}: ForeldreansvarForm) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const antall = opts?.antall || 1

	const ansvarlig = 'ansvarlig'
	const ansvarligUtenIdentifikator = 'ansvarligUtenIdentifikator'
	const nyAnsvarlig = 'nyAnsvarlig'
	const typeAnsvarlig = 'typeAnsvarlig'

	const foedselsaar =
		opts?.personFoerLeggTil?.pdl?.hentPerson?.foedselsdato?.[0].foedselsaar ||
		opts?.personFoerLeggTil?.pdl?.hentPerson?.foedsel?.[0].foedselsaar

	const kanHaForeldreansvar = !foedselsaar || new Date().getFullYear() - foedselsaar > 17

	const handleChangeTypeAnsvarlig = (target: Target, path: string) => {
		const foreldreansvar = formMethods.watch(path)
		const foreldreansvarClone = _.cloneDeep(foreldreansvar)

		_.set(foreldreansvarClone, typeAnsvarlig, target?.value || null)
		if (!target) {
			_.set(foreldreansvarClone, ansvarlig, undefined)
			_.set(foreldreansvarClone, ansvarligUtenIdentifikator, undefined)
			_.set(foreldreansvarClone, nyAnsvarlig, undefined)
		}
		if (target?.value === TypeAnsvarlig.EKSISTERENDE) {
			_.set(foreldreansvarClone, ansvarlig, null)
			_.set(foreldreansvarClone, ansvarligUtenIdentifikator, undefined)
			_.set(foreldreansvarClone, nyAnsvarlig, undefined)
		}
		if (target?.value === TypeAnsvarlig.UTEN_ID) {
			_.set(foreldreansvarClone, ansvarlig, undefined)
			_.set(foreldreansvarClone, ansvarligUtenIdentifikator, initialPdlBiPerson)
			_.set(foreldreansvarClone, nyAnsvarlig, undefined)
		}
		if (target?.value === TypeAnsvarlig.NY) {
			_.set(foreldreansvarClone, ansvarlig, undefined)
			_.set(foreldreansvarClone, ansvarligUtenIdentifikator, undefined)
			_.set(foreldreansvarClone, nyAnsvarlig, initialPdlPerson)
		}

		formMethods.setValue(path, foreldreansvarClone)
		formMethods.trigger(path)
	}

	const handleChangeAnsvar = (target: Target, path: string) => {
		const foreldreansvar = formMethods.watch(path)
		const foreldreansvarClone = _.cloneDeep(foreldreansvar)

		_.set(foreldreansvarClone, 'ansvar', target?.value || null)
		if (target?.value !== 'ANDRE') {
			_.set(foreldreansvarClone, typeAnsvarlig, undefined)
			_.set(foreldreansvarClone, ansvarlig, undefined)
			_.set(foreldreansvarClone, ansvarligUtenIdentifikator, undefined)
			_.set(foreldreansvarClone, nyAnsvarlig, undefined)
		}

		formMethods.setValue(path, foreldreansvarClone)
		formMethods.trigger(path)
	}

	const ansvar = formMethods.watch(`${path}.ansvar`)

	const getTypeAnsvarlig = () => {
		if (ansvar !== 'ANDRE') {
			return null
		}
		const type = formMethods.watch(`${path}.typeAnsvarlig`)
		if (type) {
			return type
		} else if (formMethods.watch(`${path}.ansvarlig`)) {
			return TypeAnsvarlig.EKSISTERENDE
		} else if (formMethods.watch(`${path}.nyAnsvarlig`)) {
			return TypeAnsvarlig.NY
		} else if (formMethods.watch(`${path}.ansvarligUtenIdentifikator`)) {
			return TypeAnsvarlig.UTEN_ID
		} else return null
	}

	useEffect(() => {
		if (!formMethods.watch(`${path}.typeAnsvarlig`)) {
			formMethods.setValue(`${path}.typeAnsvarlig`, getTypeAnsvarlig())
			formMethods.trigger(path)
		}
	}, [])

	const ansvarlige =
		antall > 1
			? Options('typeAnsvarlig').filter((value) => value.value !== 'EKSISTERENDE')
			: Options('typeAnsvarlig')

	return (
		<div className="flexbox--flex-wrap foreldre-form">
			<FormSelect
				name={`${path}.ansvar`}
				label="Hvem har ansvaret"
				options={Options('foreldreansvar')}
				onChange={(target: Target) => handleChangeAnsvar(target, path)}
			/>
			<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig fra og med" />
			<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig til og med" />
			{ansvar === 'ANDRE' && (
				<FormSelect
					name={`${path}.typeAnsvarlig`}
					label="Type ansvarlig"
					options={ansvarlige}
					onChange={(target: Target) => handleChangeTypeAnsvarlig(target, path)}
					size="medium"
					info={
						opts?.antall > 1 &&
						'"Eksisterende person" er kun tilgjengelig for individ, ikke for gruppe'
					}
				/>
			)}

			{getTypeAnsvarlig() === TypeAnsvarlig.EKSISTERENDE && (
				<PdlEksisterendePerson
					eksisterendePersonPath={`${path}.ansvarlig`}
					label="Ansvarlig"
					formMethods={formMethods}
					eksisterendeNyPerson={eksisterendeNyPerson}
				/>
			)}
			{ansvar === 'ANDRE' && kanHaForeldreansvar && opts?.personFoerLeggTil && (
				<PdlEksisterendePerson
					eksisterendePersonPath={`${path}.ansvarssubjekt`}
					label="Ansvarssubjekt (barn)"
					formMethods={formMethods}
					eksisterendeNyPerson={eksisterendeNyPerson}
				/>
			)}
			{getTypeAnsvarlig() === TypeAnsvarlig.UTEN_ID && (
				<PdlPersonUtenIdentifikator
					formMethods={formMethods}
					path={`${path}.ansvarligUtenIdentifikator`}
				/>
			)}
			{getTypeAnsvarlig() === TypeAnsvarlig.NY && (
				<PdlNyPerson nyPersonPath={`${path}.nyAnsvarlig`} formMethods={formMethods} />
			)}
			<AvansertForm path={path} kanVelgeMaster={false} />
		</div>
	)
}

export const Foreldreansvar = ({ formMethods }: ForeldreansvarForm) => {
	const { personFoerLeggTil, leggTilPaaGruppe } = useContext(
		BestillingsveilederContext,
	) as BestillingsveilederContextType

	const relasjoner = formMethods.watch('pdldata.person.forelderBarnRelasjon')
	const eksisterendeRelasjoner = _.get(personFoerLeggTil, 'pdl.hentPerson.forelderBarnRelasjon')

	const harBarn = () => {
		return (
			relasjoner?.some(
				(relasjon: ForeldreBarnRelasjon) => relasjon.relatertPersonsRolle === 'BARN',
			) ||
			eksisterendeRelasjoner?.some(
				(relasjon: ForeldreBarnRelasjon) => relasjon.relatertPersonsRolle === 'BARN',
			)
		)
	}

	const harBarnUtenIdentifikator = () => {
		return (
			relasjoner?.some(
				(relasjon: ForeldreBarnRelasjon) =>
					relasjon.relatertPersonsRolle === 'BARN' &&
					relasjon.relatertPersonUtenFolkeregisteridentifikator,
			) ||
			eksisterendeRelasjoner?.some(
				(relasjon: ForeldreBarnRelasjon) =>
					relasjon.relatertPersonsRolle === 'BARN' &&
					relasjon.relatertPersonUtenFolkeregisteridentifikator,
			)
		)
	}

	return (
		<>
			{!leggTilPaaGruppe && !harBarn() && !personFoerLeggTil && (
				<StyledAlert variant={'warning'} size={'small'}>
					For å sette foreldreansvar må personen også ha et barn. Det kan du legge til ved å huke av
					for Har barn/foreldre under Familierelasjoner på forrige side, og sette en relasjon av
					typen barn.
				</StyledAlert>
			)}

			{!leggTilPaaGruppe && harBarnUtenIdentifikator() && (
				<StyledAlert variant={'warning'} size={'small'}>
					Personen har ett eller flere barn uten identifikator, disse vil ikke få foreldreansvar.
				</StyledAlert>
			)}

			<FormDollyFieldArray
				name="pdldata.person.foreldreansvar"
				header={'Foreldreansvar'}
				newEntry={getInitialForeldreansvar}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => {
					return <ForeldreansvarForm formMethods={formMethods} path={path} />
				}}
			</FormDollyFieldArray>
		</>
	)
}
