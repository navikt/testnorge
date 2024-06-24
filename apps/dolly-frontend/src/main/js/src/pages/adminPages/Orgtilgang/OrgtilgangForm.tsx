import { Box, Button } from '@navikt/ds-react'
import { Form, FormProvider, SubmitHandler, useForm } from 'react-hook-form'
import { useSetOrganisasjonTilgang } from '@/utils/hooks/useOrganisasjonTilgang'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { arrayToString } from '@/utils/DataFormatter'

type Inputs = {
	organisasjonsnummer: string
	gyldigTil: string
	miljoe: string
}

const initialValues = {
	organisasjonsnummer: null,
	gyldigTil: null,
	miljoe: [],
}

const miljoer = [
	{ label: 'Q1', value: 'q1' },
	{ label: 'Q2', value: 'q2' },
]

export const OrgtilgangForm = () => {
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
		// resolver: yupResolver(validation()),
	})
	const { trigger, watch, handleSubmit, reset, control, setValue } = formMethods

	// const onSubmit: SubmitHandler<Inputs> = (data) => {
	// 	const { result, loading, error, mutate } = useSetOrganisasjonTilgang(data)
	// 	console.log('result: ', result) //TODO - SLETT MEG
	// }

	const onSubmit = async (values) => {
		const formattedValues = { ...values, miljoe: arrayToString(values.miljoe).replaceAll(' ', '') }
		console.log('formattedValues: ', formattedValues) //TODO - SLETT MEG
		await OrganisasjonTilgangService.postOrganisasjon(formattedValues).then((response) => {
			console.log('response: ', response) //TODO - SLETT MEG
		})
	}

	return (
		<Box background="surface-default" padding="4" style={{ marginBottom: '15px' }}>
			<h2 style={{ marginTop: '5px' }}>Opprett tilgang</h2>
			<FormProvider {...formMethods}>
				<Form control={control} autoComplete={'off'} onSubmit={handleSubmit(onSubmit)}>
					<div className="flexbox--flex-wrap">
						<FormTextInput
							name={'organisasjonsnummer'}
							label={'Organisasjonsnummer'}
							size="large"
						/>
						<FormSelect
							name={'miljoe'}
							label={'Miljø'}
							options={miljoer}
							isMulti={true}
							size="medium"
						/>
						<FormDatepicker name={'gyldigTil'} label={'Gyldig til'} />
					</div>
					<Button type="submit" variant="primary">
						Opprett
					</Button>
				</Form>
			</FormProvider>
		</Box>
	)
}
