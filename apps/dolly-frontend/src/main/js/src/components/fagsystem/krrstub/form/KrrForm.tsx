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
import { FieldValues, UseFormReturn } from 'react-hook-form/dist/types'

type KrrstubFormProps = {
	formMethods: UseFormReturn<FieldValues, any, undefined>
}

type Change = {
	value: boolean
}

export const krrAttributt = 'krrstub'

export const KrrstubForm = ({ formMethods }: KrrstubFormProps) => {
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
				hasErrors={panelError(formMethods.formState.errors, krrAttributt)}
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
							<FormikTextInput
								name="krrstub.mobil"
								label="Mobilnummer (+47)"
								placeholder={'+4712345678'}
							/>
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

KrrstubForm.validation = KrrValidation
