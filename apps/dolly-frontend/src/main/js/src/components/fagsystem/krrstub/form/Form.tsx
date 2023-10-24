import * as Yup from 'yup'
import { ifPresent, requiredBoolean } from '@/utils/YupValidations'
import * as _ from 'lodash-es'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { FormikProps } from 'formik'

type KrrstubFormProps = {
	formikBag: FormikProps<{}>
}

type Change = {
	value: boolean
}

export const krrAttributt = 'krrstub'

export const KrrstubForm = ({ formikBag }: KrrstubFormProps) => {
	const leverandoerer = SelectOptionsOppslag.hentKrrLeverandoerer()
	//@ts-ignore
	const leverandoerOptions = SelectOptionsOppslag.formatOptions('sdpLeverandoer', leverandoerer)
	const registrert = _.get(formikBag, 'values.krrstub.registrert')

	const handleRegistrertChange = (newRegistrert: Change) => {
		if (!newRegistrert.value) {
			formikBag.setFieldValue('krrstub', {
				epost: '',
				gyldigFra: null,
				mobil: '',
				sdpAdresse: '',
				sdpLeverandoer: '',
				spraak: '',
				registrert: newRegistrert.value,
				reservert: null,
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
				startOpen={erForsteEllerTest(formikBag.values, [krrAttributt])}
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
							TODO: FIKSE DENNE TIL Å VISE TYDELIG AT +47xxx ER RIKTIG FORMAT
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
					<Kategori title={'Sikker digital postkasse'}>
						<div className="flexbox--flex-wrap">
							<FormikTextInput name="krrstub.sdpAdresse" label="Adresse" />
							<FormikSelect
								fastfield={false}
								name="krrstub.sdpLeverandoer"
								label="Leverandør"
								options={leverandoerOptions}
							/>
						</div>
					</Kategori>
				)}
			</Panel>
		</Vis>
	)
}

KrrstubForm.validation = {
	krrstub: Yup.object({
		epost: Yup.string(),
		gyldigFra: Yup.date().nullable(),
		mobil: Yup.string().matches(/^\d*$/, 'Ugyldig mobilnummer'),
		sdpAdresse: Yup.string(),
		sdpLeverandoer: Yup.string().nullable(),
		spraak: Yup.string(),
		registrert: ifPresent('$krrstub.registrert', requiredBoolean),
		reservert: Yup.boolean().nullable(),
	}),
}
