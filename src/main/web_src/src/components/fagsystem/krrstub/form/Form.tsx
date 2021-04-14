import React from 'react'
import * as Yup from 'yup'
import { requiredBoolean } from '~/utils/YupValidations'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect, DollySelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikProps } from 'formik'

type KrrstubForm = {
	formikBag: FormikProps<{}>
}

type Change = {
	value: boolean
}

const krrAttributt = 'krrstub'

export const KrrstubForm = ({ formikBag }: KrrstubForm) => {
	const leverandoerer = SelectOptionsOppslag.hentKrrLeverandoerer()
	//@ts-ignore
	const leverandoerOptions = SelectOptionsOppslag.formatOptions('sdpLeverandoer', leverandoerer)
	const registrert = _get(formikBag, 'values.krrstub.registrert')

	const handleRegistrertChange = (registrert: Change) => {
		if (!registrert.value) {
			formikBag.setFieldValue('krrstub', {
				epost: '',
				gyldigFra: null,
				mobil: '',
				sdpAdresse: '',
				sdpLeverandoer: '',
				spraak: '',
				registrert: registrert.value,
				reservert: null
			})
		} else {
			formikBag.setFieldValue('krrstub.registrert', true)
		}
	}

	return (
		//@ts-ignore
		<Vis attributt={krrAttributt}>
			<Panel
				heading="Kontakt- og reservasjonsregisteret"
				hasErrors={panelError(formikBag, krrAttributt)}
				iconType="krr"
				//@ts-ignore
				startOpen={() => erForste(formikBag.values, [krrAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<DollySelect
						name="krrstub.registrert"
						label="Registrert i KRR"
						options={Options('boolean')}
						onChange={handleRegistrertChange}
						value={registrert}
						isClearable={false}
						feil={registrert === null && { feilmelding: 'Feltet er påkrevd' }}
					/>
					{registrert && (
						<>
							<FormikSelect
								name="krrstub.reservert"
								label="Reservert"
								options={Options('boolean')}
								fastfield={false}
							/>
							<FormikTextInput name="krrstub.epost" label="E-post" />
							<FormikTextInput name="krrstub.mobil" label="Mobilnummer" type="number" />
							<FormikSelect
								name="krrstub.spraak"
								label="Språk"
								options={Options('spraaktype')}
								fastfield={false}
							/>
							<FormikDatepicker
								name="krrstub.gyldigFra"
								label="Kontaktinfo gjelder fra"
								fastfield={false}
							/>
						</>
					)}
				</div>
				{registrert && (
					<div className="flexbox--flex-wrap">
						<Kategori title={'Sikker digital postkasse'}>
							<FormikTextInput name="krrstub.sdpAdresse" label="Adresse" />
							<FormikSelect
								fastfield={false}
								name="krrstub.sdpLeverandoer"
								label="Leverandør"
								options={leverandoerOptions}
							/>
						</Kategori>
					</div>
				)}
			</Panel>
		</Vis>
	)
}

KrrstubForm.validation = {
	krrstub: Yup.object({
		epost: Yup.string(),
		gyldigFra: Yup.date().nullable(),
		mobil: Yup.string().matches(/^[0-9]*$/, 'Ugyldig mobilnummer'),
		sdpAdresse: Yup.string(),
		sdpLeverandoer: Yup.string(),
		spraak: Yup.string(),
		registrert: requiredBoolean,
		reservert: Yup.boolean().nullable()
	})
}
