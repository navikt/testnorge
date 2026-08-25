import { Form, FormProvider } from 'react-hook-form'
import styled from 'styled-components'
import { Tabs } from '@navikt/ds-react'
import React, { lazy, Suspense } from 'react'
import { useErDollyAdmin } from '@/utils/DollyAdmin'
import { FolkeregisteretTab } from '@/pages/tenorSoek/soekFormTabs/FolkeregisteretTab'
import { PensjonTab } from '@/pages/tenorSoek/soekFormTabs/PensjonTab'
import { VirksomhetTab } from '@/pages/tenorSoek/soekFormTabs/VirksomhetTab'
import { SkattTab } from '@/pages/tenorSoek/soekFormTabs/SkattTab'
import { ArbeidInntektTab } from '@/pages/tenorSoek/soekFormTabs/ArbeidInntektTab'
import { AntallCircle, getAntallRequest } from '@/components/ui/soekForm/SoekFormWrapper'
import { tabPaths } from '@/pages/tenorSoek/soekFormTabs/soekFormPaths'

const DisplayFormState = lazy(() => import('@/utils/DisplayFormState'))

const SoekefeltWrapper = styled.div`
	display: flex;
	flex-direction: column;
	margin-bottom: 20px;
	background-color: white;
	border: 1px solid @color-bg-grey-border;
	border-radius: 4px;
	width: 100%;
	padding: 15px 0 0 0;
`

const TabLabel = styled.span`
	display: flex;
	align-items: center;
`

export const SoekForm = ({ formMethods, handleChange, handleChangeList, emptyCategory }: any) => {
	const { getValues, control, watch }: any = formMethods
	const isAdmin = useErDollyAdmin()

	const devEnabled =
		window.location.hostname.includes('localhost') ||
		window.location.hostname.includes('dolly-frontend-dev')

	watch()

	const tabLabel = (label: string, paths: string[]) => (
		<TabLabel>
			{label}
			<AntallCircle antall={getAntallRequest(paths, getValues)} />
		</TabLabel>
	)

	return (
		<Tabs defaultValue="folkeregisteret" style={{ width: '100%' }}>
			<Tabs.List>
				<Tabs.Tab
					value="folkeregisteret"
					label={tabLabel('Folkeregisteret', tabPaths.folkeregisteret)}
				/>
				<Tabs.Tab value="skatt" label={tabLabel('Skatt', tabPaths.skatt)} />
				<Tabs.Tab
					value="arbeidinntekt"
					label={tabLabel('Arbeid og inntekt', tabPaths.arbeidinntekt)}
				/>
				<Tabs.Tab value="pensjon" label={tabLabel('Pensjon', tabPaths.pensjon)} />
				<Tabs.Tab value="virksomhet" label={tabLabel('Virksomhet', tabPaths.virksomhet)} />
			</Tabs.List>
			<SoekefeltWrapper>
				<FormProvider {...formMethods}>
					<>
						<Form control={control} className="flexbox--flex-wrap">
							<FolkeregisteretTab
								handleChange={handleChange}
								handleChangeList={handleChangeList}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
							<SkattTab
								handleChange={handleChange}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
							<ArbeidInntektTab
								handleChange={handleChange}
								handleChangeList={handleChangeList}
								getValues={getValues}
								emptyCategory={emptyCategory}
								watch={watch}
							/>
							<PensjonTab
								handleChange={handleChange}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
							<VirksomhetTab
								handleChangeList={handleChangeList}
								getValues={getValues}
								emptyCategory={emptyCategory}
							/>
						</Form>
						{(devEnabled || isAdmin) && (
							<Suspense fallback={null}>
								<DisplayFormState />
							</Suspense>
						)}
					</>
				</FormProvider>
			</SoekefeltWrapper>
		</Tabs>
	)
}
