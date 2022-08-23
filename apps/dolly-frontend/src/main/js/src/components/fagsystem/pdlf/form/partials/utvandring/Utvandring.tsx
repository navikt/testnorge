import React, { useContext } from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { initialUtvandring } from '~/components/fagsystem/pdlf/form/initialValues'
import { AdresseKodeverk } from '~/config/kodeverk'
import { DatepickerWrapper } from '~/components/ui/form/inputs/datepicker/DatepickerStyled'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { InnflyttingTilNorge } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { getSisteDato } from '~/components/bestillingsveileder/utils'

type UtvandringTypes = {
	path: string
	minDate?: Date
}

export const UtvandringForm = ({ path, minDate }: UtvandringTypes) => {
	return (
		<>
			<FormikSelect
				name={`${path}.tilflyttingsland`}
				label="Utvandret til"
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				size="large"
				isClearable={false}
			/>
			<FormikTextInput name={`${path}.tilflyttingsstedIUtlandet`} label="Tilflyttingssted" />
			<DatepickerWrapper>
				<FormikDatepicker
					name={`${path}.utflyttingsdato`}
					label="Utflyttingsdato"
					minDate={minDate}
				/>
			</DatepickerWrapper>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Utvandring = () => {
	const opts = useContext(BestillingsveilederContext)

	const sisteDatoInnflytting = () => {
		if (opts.is.leggTil) {
			const innflytting = opts?.personFoerLeggTil?.pdl?.hentPerson?.innflyttingTilNorge
			let siste = getSisteDato(
				innflytting?.map(
					(val: InnflyttingTilNorge) => new Date(val.folkeregistermetadata?.gyldighetstidspunkt)
				)
			)
			siste.setDate(siste.getDate() + 1)
			return siste
		}
		return null
	}

	const datoBegresning = sisteDatoInnflytting()
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.utflytting'}
				header="Utvandring"
				newEntry={initialUtvandring}
				canBeEmpty={false}
				maxEntries={1}
			>
				{(path: string, _idx: number) => <UtvandringForm path={path} minDate={datoBegresning} />}
			</FormikDollyFieldArray>
		</div>
	)
}
