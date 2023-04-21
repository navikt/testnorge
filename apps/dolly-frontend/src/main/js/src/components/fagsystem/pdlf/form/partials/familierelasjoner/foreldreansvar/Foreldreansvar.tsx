import * as React from 'react'
import { FormikProps } from 'formik'
import {
	initialForeldreansvar,
	initialPdlBiPerson,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as _ from 'lodash-es'
import { ForeldreBarnRelasjon, TypeAnsvarlig } from '@/components/fagsystem/pdlf/PdlTypes'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { PdlNyPerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { PdlPersonUtenIdentifikator } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonUtenIdentifikator'
import { Alert } from '@navikt/ds-react'
import { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/Bestillingsveileder'
import styled from 'styled-components'
import _get from 'lodash/get'

interface ForeldreansvarForm {
	formikBag: FormikProps<{}>
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

export const ForeldreansvarForm = ({ formikBag, path }: ForeldreansvarForm) => {
	// console.log('formikBag.values foreldreansvar: ', formikBag.values) //TODO - SLETT MEG
	// console.log('path: ', path) //TODO - SLETT MEG
	// if (!_get(formikBag.values, 'foreldreansvar')) {
	// 	return null
	// }
	//TODO FJERN DENNE!
	const ansvarlig = 'ansvarlig'
	const ansvarligUtenIdentifikator = 'ansvarligUtenIdentifikator'
	const nyAnsvarlig = 'nyAnsvarlig'
	const typeAnsvarlig = 'typeAnsvarlig'
	const handleChangeTypeAnsvarlig = (target: Target, path: string) => {
		const foreldreansvar = _.get(formikBag.values, path)
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

		formikBag.setFieldValue(path, foreldreansvarClone)
	}

	const handleChangeAnsvar = (target: Target, path: string) => {
		const foreldreansvar = _.get(formikBag.values, path)
		const foreldreansvarClone = _.cloneDeep(foreldreansvar)

		_.set(foreldreansvarClone, 'ansvar', target?.value || null)
		if (target?.value !== 'ANDRE') {
			_.set(foreldreansvarClone, typeAnsvarlig, undefined)
			_.set(foreldreansvarClone, ansvarlig, undefined)
			_.set(foreldreansvarClone, ansvarligUtenIdentifikator, undefined)
			_.set(foreldreansvarClone, nyAnsvarlig, undefined)
		}

		formikBag.setFieldValue(path, foreldreansvarClone)
	}

	const ansvarligType = _.get(formikBag.values, `${path}.typeAnsvarlig`)
	const ansvar = _.get(formikBag.values, `${path}.ansvar`)

	return (
		<div className="flexbox--flex-wrap">
			{!path?.includes('pdldata') && (
				<h4 style={{ marginTop: '15px', marginBottom: '15px', width: '100%' }}>Foreldreansvar</h4>
			)}
			<FormikSelect
				name={`${path}.ansvar`}
				label="Hvem har ansvaret"
				options={Options('foreldreansvar')}
				onChange={(target: Target) => handleChangeAnsvar(target, path)}
			/>
			<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig fra og med" />
			<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig til og med" />

			{ansvar === 'ANDRE' && (
				<FormikSelect
					name={`${path}.typeAnsvarlig`}
					label="Type ansvarlig"
					options={Options('typeAnsvarlig')}
					onChange={(target: Target) => handleChangeTypeAnsvarlig(target, path)}
					size="medium"
				/>
			)}

			{ansvarligType === TypeAnsvarlig.EKSISTERENDE && (
				<PdlEksisterendePerson
					eksisterendePersonPath={`${path}.ansvarlig`}
					label="Ansvarlig"
					formikBag={formikBag}
				/>
			)}

			{ansvarligType === TypeAnsvarlig.UTEN_ID && (
				<PdlPersonUtenIdentifikator
					formikBag={formikBag}
					path={`${path}.ansvarligUtenIdentifikator`}
				/>
			)}

			{ansvarligType === TypeAnsvarlig.NY && (
				<PdlNyPerson nyPersonPath={`${path}.nyAnsvarlig`} formikBag={formikBag} />
			)}

			<AvansertForm path={path} kanVelgeMaster={false} />
		</div>
	)
}

export const Foreldreansvar = ({ formikBag }: ForeldreansvarForm) => {
	const { personFoerLeggTil, leggTilPaaGruppe } = useContext(BestillingsveilederContext)

	const harBarn = () => {
		const relasjoner = _.get(formikBag.values, 'pdldata.person.forelderBarnRelasjon')
		const eksisterendeRelasjoner = _.get(personFoerLeggTil, 'pdl.hentPerson.forelderBarnRelasjon')
		return (
			relasjoner?.some(
				(relasjon: ForeldreBarnRelasjon) => relasjon.relatertPersonsRolle === 'BARN'
			) ||
			eksisterendeRelasjoner?.some(
				(relasjon: ForeldreBarnRelasjon) => relasjon.relatertPersonsRolle === 'BARN'
			)
		)
	}

	return (
		<>
			{!leggTilPaaGruppe && !harBarn() && (
				<StyledAlert variant={'warning'} size={'small'}>
					For å sette foreldreansvar må personen også ha et barn. Det kan du legge til ved å huke av
					for Har barn/foreldre under Familierelasjoner på forrige side, og sette en relasjon av
					typen barn.
				</StyledAlert>
			)}
			<FormikDollyFieldArray
				name="pdldata.person.foreldreansvar"
				header={'Foreldreansvar'}
				newEntry={initialForeldreansvar}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => {
					return <ForeldreansvarForm formikBag={formikBag} path={path} />
				}}
			</FormikDollyFieldArray>
		</>
	)
}
