import { Box, Button, Search, ToggleGroup } from '@navikt/ds-react'
import React, { useState } from 'react'
import { PersonIcon, TenancyIcon } from '@navikt/aksel-icons'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { getIdentUrl } from '@/utils/hooks/useLevendeArbeidsforhold'
import Request from '@/service/services/Request'

enum SoekKategorier {
	IDENT = 'ident',
	ORGNR = 'orgnr',
}

export const NyansettelserSoek = ({ setIdentSoekData, setPage }) => {
	const [soekKategori, setSoekKategori] = useState(SoekKategorier.IDENT)
	const [soekValue, setSoekValue] = useState(null)

	const onSubmit = async () => {
		await Request.get(getIdentUrl(soekValue)).then((response) => {
			setIdentSoekData(response.data)
			setPage(1)
		})
	}

	const formMethods = useForm({
		mode: 'onSubmit',
		// resolver: yupResolver(validation()),
	})

	const nullstill = () => {
		formMethods.reset()
		setSoekValue(null)
		setIdentSoekData(null)
		setPage(1)
	}

	return (
		<Box background="surface-default" padding="4">
			<ToggleGroup
				defaultValue={soekKategori}
				onChange={(kategori: string) => setSoekKategori(kategori)}
				size="small"
			>
				<ToggleGroup.Item
					value={SoekKategorier.IDENT}
					icon={<PersonIcon fontSize="1.5rem" />}
					label="Søk etter personident"
				/>
				<ToggleGroup.Item
					value={SoekKategorier.ORGNR}
					icon={<TenancyIcon fontSize="1.5rem" />}
					label="Søk etter organisasjonsnummer"
				/>
			</ToggleGroup>
			<FormProvider {...formMethods}>
				<Form
					control={formMethods.control}
					// className={'opprett-tabellrad'}
					autoComplete={'off'}
					onSubmit={formMethods.handleSubmit(onSubmit)}
				>
					<div className="flexbox--flex-wrap" style={{ marginTop: '15px' }}>
						<Search
							label="Søk etter personident"
							variant="secondary"
							placeholder="Søk etter personident ..."
							onChange={(value) => {
								value ? setSoekValue(value) : setSoekValue(null)
							}}
							onClear={nullstill}
						/>
						{/*<Button variant="tertiary" size="small" onClick={nullstill}>*/}
						{/*	Nullstill*/}
						{/*</Button>*/}
					</div>
				</Form>
			</FormProvider>
		</Box>
	)
}
