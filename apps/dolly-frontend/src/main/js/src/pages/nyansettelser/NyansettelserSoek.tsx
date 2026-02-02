import { Box, Search, ToggleGroup } from '@navikt/ds-react'
import React, { useState } from 'react'
import { PersonIcon, TenancyIcon } from '@navikt/aksel-icons'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { getIdentUrl, getOrgnummerUrl } from '@/utils/hooks/useLevendeArbeidsforhold'
import Request from '@/service/services/Request'

enum SoekKategorier {
	IDENT = 'ident',
	ORGNR = 'orgnr',
}

export const NyansettelserSoek = ({ setIdentSoekData, setOrgnummerSoekData, setPage }: any) => {
	const [soekKategori, setSoekKategori] = useState(SoekKategorier.IDENT)
	const [soekValue, setSoekValue] = useState(null)

	const onSubmit = async () => {
		if (!soekValue) {
			nullstill()
			return
		}
		if (soekKategori === SoekKategorier.ORGNR) {
			await Request.get(getOrgnummerUrl(soekValue)).then((response: any) => {
				setOrgnummerSoekData(response.data)
				setPage(1)
			})
		} else {
			await Request.get(getIdentUrl(soekValue)).then((response: any) => {
				setIdentSoekData(response.data)
				setPage(1)
			})
		}
	}

	const formMethods = useForm({
		mode: 'onSubmit',
	})

	const nullstill = () => {
		setSoekValue(null)
		setIdentSoekData(null)
		setOrgnummerSoekData(null)
		setPage(1)
	}

	return (
		<Box background="default" padding="space-16">
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
				<Form onSubmit={formMethods.handleSubmit(onSubmit)}>
					<div className="flexbox--flex-wrap" style={{ marginTop: '15px' }}>
						<Search
							label="Søk etter personident"
							variant="secondary"
							placeholder={
								soekKategori === SoekKategorier.ORGNR
									? 'Søk etter organisasjonsnummer ...'
									: 'Søk etter personident ...'
							}
							onChange={(value) => {
								value ? setSoekValue(value) : setSoekValue(null)
							}}
							onClear={nullstill}
						/>
					</div>
				</Form>
			</FormProvider>
		</Box>
	)
}
