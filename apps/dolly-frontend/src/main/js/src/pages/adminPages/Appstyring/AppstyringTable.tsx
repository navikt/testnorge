import { Box, Table } from '@navikt/ds-react'
import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { EditParameter } from '@/pages/adminPages/Appstyring/EditParameter'
import {multiFetcherPensjon} from "@/api";
import {ifPresent} from "@/utils/YupValidations";

export const AppstyringTable = ({ data }: any) => {
	//TODO: Implementer henting av options pr parameter fra backend
	if (data.contains("Organisasjon")) {
		//useEffect(() => {
		//	fetch(url)
		//}, []);
	}
	if (data.contains("Person")){

	}
	if (data.contains("Arbeidsforhold")){

	}

	const getOptions = (parameter: string) => {
		return [
			{ value: 'verdi1', label: 'Verdi 1' },
			{ value: 'verdi2', label: 'Verdi 2' },
			{ value: 'verdi3', label: 'Verdi 3' },
			{ value: 'verdi4', label: 'Verdi 4' },
			{ value: 'verdi5', label: 'Verdi 5' },
			{ value: 'verdi6', label: 'Verdi 6' },
			{ value: 'verdi7', label: 'Verdi 7' },
			{ value: 'verdi8', label: 'Verdi 8' },
			{ value: 'verdi9', label: 'Verdi 9' },
			{ value: 'verdi10', label: 'Verdi 10' },
		]
	}

	return (
		<Box background="surface-default" padding="4">
			<ErrorBoundary>
				<Table>
					<Table.Header>
						<Table.HeaderCell>Parameter</Table.HeaderCell>
						<Table.HeaderCell>Verdi</Table.HeaderCell>
						<Table.HeaderCell>Rediger</Table.HeaderCell>
					</Table.Header>
					<Table.Body>
						{data.map((row: any, idx: number) => {
							return (
								<Table.Row key={row.parameter + idx}>
									<Table.DataCell width={'50%'}>{row.parameter}</Table.DataCell>
									<Table.DataCell width={'40%'}>{row.verdi}</Table.DataCell>
									<Table.DataCell width={'10%'}>
										<EditParameter
											name={row.parameter}
											initialValue={row.verdi}
											getOptions={getOptions}
										/>
									</Table.DataCell>
								</Table.Row>
							)
						})}
					</Table.Body>
				</Table>
			</ErrorBoundary>
		</Box>
	)
}
