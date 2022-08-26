import React, { useContext } from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '~/config/kodeverk'
import { DatepickerWrapper } from '~/components/ui/form/inputs/datepicker/DatepickerStyled'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { UtflyttingFraNorge } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { getSisteDato } from '~/components/bestillingsveileder/utils'

type InnvandringTypes = {
	path: string
	minDate?: Date
}

export const InnvandringForm = ({ path, minDate }: InnvandringTypes) => {
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
			const utflytting = opts?.personFoerLeggTil?.pdl?.hentPerson?.utflyttingFraNorge
			let siste = getSisteDato(
				utflytting?.map((val: UtflyttingFraNorge) => new Date(val.utflyttingsdato))
			)
			if (siste !== null) {
				siste.setDate(siste.getDate() + 1)
			}
			return siste
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
