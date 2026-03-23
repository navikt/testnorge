import { Alert, Tabs } from '@navikt/ds-react'
import styled from 'styled-components'
import React, { useState } from 'react'

const StyledTabs = styled(Tabs)`
	margin-top: -10px;
	margin-bottom: 20px;
`

const StyledPanel = styled(Tabs.Panel)`
	margin-top: 10px;
`

const BestiltMiljoTab = styled(Tabs.Tab)`
	color: #06893a;

	&&& {
		.aksel-tabs__tab-inner {
			color: #06893a;
		}
	}
`

const ErrorMiljoTab = styled(Tabs.Tab)`
	&&& {
		.aksel-tabs__tab-inner {
			color: #ba3a26;
		}
	}
`

export const MiljoTabs = ({ bestilteMiljoer, errorMiljoer = [], forsteMiljo, data, children }) => {
	const [isPanelOpen, setPanelOpen] = useState()

	return (
		<StyledTabs size="small" defaultValue={forsteMiljo}>
			<Tabs.List>
				{data?.map?.((miljoData, idx: number) => {
					if (errorMiljoer?.includes(miljoData?.miljo)) {
						return (
							<ErrorMiljoTab
								key={miljoData?.miljo + idx}
								value={miljoData?.miljo}
								label={miljoData?.miljo?.toUpperCase() || '?'}
							/>
						)
					}
					if (bestilteMiljoer?.includes(miljoData?.miljo)) {
						return (
							<BestiltMiljoTab
								key={miljoData?.miljo + idx}
								value={miljoData?.miljo}
								label={miljoData?.miljo?.toUpperCase() || '?'}
							/>
						)
					}
					return (
						<Tabs.Tab
							key={miljoData?.miljo + idx}
							value={miljoData?.miljo}
							label={miljoData?.miljo?.toUpperCase() || '?'}
						/>
					)
				})}
			</Tabs.List>
			{data?.map?.((miljoData, idx: number) => (
				<StyledPanel key={miljoData?.miljo + idx} value={miljoData?.miljo}>
					{!miljoData?.data ||
					miljoData?.data?.length < 1 ||
					miljoData?.data?.inntekter?.length === 0 ? (
						<Alert variant="info" size="small" inline>
							{miljoData?.info ? miljoData.info : 'Fant ingen data i dette miljøet'}
						</Alert>
					) : (
						React.cloneElement(children, {
							data: miljoData?.data,
							miljo: miljoData?.miljo,
							isPanelOpen: isPanelOpen,
							setPanelOpen: setPanelOpen,
						})
					)}
				</StyledPanel>
			))}
		</StyledTabs>
	)
}
