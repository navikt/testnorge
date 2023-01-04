import React, { useContext } from 'react'
// @ts-ignore
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '@/config/kodeverk'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/Bestillingsveileder'
import { Utflytting } from '@/components/fagsystem/pdlf/PdlTypes'
import { getSisteDato } from '@/components/bestillingsveileder/utils'
import { FormikProps } from 'formik'
import * as _ from 'lodash-es'
import {
	getFirstDateAfter,
	getLastDateBefore,
} from '@/components/fagsystem/pdlf/form/partials/utvandring/utils'

type InnvandringTypes = {
	path: string
	minDate?: Date
	maxDate?: Date
}
type RedigerTypes = {
	formikBag: FormikProps<{}>
	path: string
	personFoerLeggTil: any
}

export const RedigerInnvandringForm = ({ formikBag, path, personFoerLeggTil }: RedigerTypes) => {
	const hoveddato = new Date(_.get(formikBag.values, path)?.innflyttingsdato)
	const datoer = personFoerLeggTil?.pdldata?.person?.utflytting?.map(
		(utflytting: any) => new Date(utflytting.utflyttingsdato)
	)
	const minDate = getLastDateBefore(hoveddato, datoer)
	const maxDate = getFirstDateAfter(hoveddato, datoer)
	return <InnvandringForm path={path} minDate={minDate} maxDate={maxDate} />
}

const InnvandringForm = ({ path, minDate, maxDate }: InnvandringTypes) => {
	return (
		<>
			<FormikSelect
				name={`${path}.fraflyttingsland`}
				label="Innvandret fra"
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				size="large"
				isClearable={false}
			/>
			<FormikTextInput name={`${path}.fraflyttingsstedIUtlandet`} label="Fraflyttingssted" />
			<DatepickerWrapper>
				<FormikDatepicker
					name={`${path}.innflyttingsdato`}
					label="Innflyttingsdato"
					minDate={minDate}
					maxDate={maxDate}
				/>
			</DatepickerWrapper>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Innvandring = () => {
	const opts = useContext(BestillingsveilederContext)

	const sisteDatoUtvandring = () => {
		if (opts.is.leggTil) {
			const utflytting = opts?.personFoerLeggTil?.pdlforvalter?.person?.utflytting
			if (utflytting) {
				let siste = getSisteDato(
					utflytting?.map((val: Utflytting) => new Date(val.utflyttingsdato))
				)
				if (siste !== null) {
					siste.setDate(siste.getDate() + 1)
				}
				return siste
			}
		}
		return null
	}

	const datoBegresning = sisteDatoUtvandring()

	return (
		<div className="person-visning_content">
			<InnvandringForm path={'pdldata.person.innflytting[0]'} minDate={datoBegresning} />
		</div>
	)
}
