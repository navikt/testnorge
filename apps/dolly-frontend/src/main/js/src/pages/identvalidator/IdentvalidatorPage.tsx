import { useValiderIdent } from '@/utils/hooks/useIdentPool'
import { Alert, Box, Textarea, VStack } from '@navikt/ds-react'
import React, { useEffect, useState } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { IdentvalidatorVisning } from '@/pages/identvalidator/IdentvalidatorVisning'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import Loading from '@/components/ui/loading/Loading'

const initialValues = {
	ident: '',
}

export default () => {
	const [ident, setIdent] = useState('')
	const { validering, loading, error } = useValiderIdent(ident)

	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
	})

	useEffect(() => {
		if (validering && !loading && !error) {
			formMethods.reset(initialValues)
		}
	}, [validering])

	const handleValidate = (data: { ident: string }) => {
		setIdent(data?.ident)
	}

	const { currentBruker, loading: currenBrukerLoading } = useCurrentBruker()
	if (currenBrukerLoading) {
		return <Loading label="Sjekker tilgang ..." />
	}
	if (currentBruker?.brukertype !== 'AZURE') {
		return <AdminAccessDenied />
	}

	console.log('formMethods.watch(): ', formMethods.watch()) //TODO - SLETT MEG

	return (
		<>
			<h1>Valider identer</h1>
			<VStack gap="space-16">
				<Box background={'surface-default'} padding="6">
					<FormProvider {...formMethods}>
						<form onSubmit={formMethods.handleSubmit(handleValidate)}>
							<Textarea
								name="ident"
								label="Identer"
								value={formMethods.watch('ident')}
								onChange={(event) => formMethods.setValue('ident', event.target.value)}
								description="Skriv inn én eller flere identer, adskilt med mellomrom, komma, semikolon eller linjeskift."
								resize="vertical"
							/>
							<NavButton
								variant={'primary'}
								type={'submit'}
								loading={loading}
								style={{ marginTop: '15px' }}
							>
								Valider
							</NavButton>
						</form>
					</FormProvider>
				</Box>
				{error && (
					<Alert variant={'error'} size={'small'}>
						Feil ved validering: {error.message}
					</Alert>
				)}
				<IdentvalidatorVisning data={validering} />
			</VStack>
		</>
	)
}
