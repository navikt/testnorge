import { useValiderIdent } from '@/utils/hooks/useIdentPool'
import { VStack } from '@navikt/ds-react'
import { useState } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { IdentvalidatorVisning } from '@/pages/identvalidator/IdentvalidatorVisning'

const initialValues = {
	ident: '',
}

export default () => {
	const [ident, setIdent] = useState('')
	const { validering, loading, error } = useValiderIdent(ident)
	console.log('validering: ', validering) //TODO - SLETT MEG

	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
		// resolver: yupResolver(validation),
	})

	const handleValidate = (data: any) => {
		console.log('data: ', data) //TODO - SLETT MEG
		setIdent(data?.ident)
		formMethods.reset(initialValues)
	}

	return (
		<>
			<h1>Valider fødselsnummer</h1>
			<p>Skriv inn en test-ident for å validere.</p>
			<VStack gap="space-16">
				<FormProvider {...formMethods}>
					<form onSubmit={formMethods.handleSubmit(handleValidate)}>
						<div className="flexbox--flex-wrap">
							<DollyTextInput name="ident" />
							<NavButton variant={'primary'} type={'submit'} loading={loading}>
								Valider
							</NavButton>
						</div>
					</form>
				</FormProvider>
				<IdentvalidatorVisning data={validering} />
			</VStack>
		</>
	)
}
