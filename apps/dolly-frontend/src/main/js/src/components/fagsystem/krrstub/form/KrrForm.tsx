import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { KrrValidation } from '@/components/fagsystem/krrstub/form/KrrValidation'
import { useFormContext } from 'react-hook-form'

type Change = {
	value: boolean
}

export const krrAttributt = 'krrstub'

export const KrrstubForm = () => {
	const formMethods = useFormContext()
	const leverandoerer = SelectOptionsOppslag.hentKrrLeverandoerer()
	//@ts-ignore
	const leverandoerOptions = SelectOptionsFormat.formatOptions('sdpLeverandoer', leverandoerer)
	const registrert = formMethods.watch('krrstub.registrert')

	const handleRegistrertChange = (newRegistrert: Change) => {
		if (!newRegistrert.value) {
			formMethods.setValue('krrstub', {
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
			formMethods.setValue('krrstub.registrert', true)
		}
	}

	return (
		//@ts-ignore
		<Vis attributt={krrAttributt}>
			<Panel
				heading="Kontakt- og reservasjonsregisteret"
				hasErrors={panelError(krrAttributt)}
				iconType="krr"
				startOpen={erForsteEllerTest(formMethods.getValues(), [krrAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<DollySelect
						name="krrstub.registrert"
						label="Registrert i KRR"
						options={Options('boolean')}
						onChange={handleRegistrertChange}
						value={registrert}
						isClearable={false}
					/>
					{registrert && (
						<>
							<FormikSelect
								name="krrstub.reservert"
								label="Reservert"
								options={Options('boolean')}
							/>
							<FormikTextInput name="krrstub.epost" label="E-post" />
							<FormikTextInput
								name="krrstub.mobil"
								label="Mobilnummer (+47)"
								placeholder={'+4712345678'}
							/>
							<FormikSelect name="krrstub.spraak" label="Språk" options={Options('spraaktype')} />
							<FormikDatepicker name="krrstub.gyldigFra" label="Kontaktinfo gjelder fra" />
						</>
					)}
				</div>
				{registrert && (
					<Kategori title={'Sikker digital postkasse'}>
						<div className="flexbox--flex-wrap">
							<FormikTextInput name="krrstub.sdpAdresse" label="Adresse" />
							<FormikSelect
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
		mobil: Yup.string().matches(/^\+?\d{8,14}$/, {
			message: 'Ugyldig telefonnummer',
			excludeEmptyString: true,
		}),
		sdpAdresse: Yup.string(),
		sdpLeverandoer: Yup.string().nullable(),
		spraak: Yup.string(),
		registrert: ifPresent('$krrstub.registrert', requiredBoolean),
		reservert: Yup.boolean().nullable(),
	}),
}
