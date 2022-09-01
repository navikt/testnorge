import React, { useState } from 'react'
import './DataVisning.less'
import 'rc-tooltip/assets/bootstrap_white.css'
import Tooltip from 'rc-tooltip'
import { useBoolean } from 'react-use'
import { DollyApi } from '~/service/Api'
import Icon from '~/components/ui/icon/Icon'
import { PdlVisning } from '~/components/fagsystem/pdl/visning/PdlVisning'
import { Ident, PdlDataWrapper } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type PdlDataVisningProps = {
	ident: Ident
}

export const PdlDataVisning = ({ ident }: PdlDataVisningProps) => {
	const [pdlData, setPdlData] = useState(null)
	const [pdlDataQ1, setPdlDataQ1] = useState(null)
	const [pdlLoading, setPdlLoading] = useBoolean(true)
	const [pdlError, setPdlError] = useState(null)

	const getPersonInfo = (pdlMiljoe = null as string) => {
		if ((!pdlData && !pdlMiljoe) || (!pdlDataQ1 && pdlMiljoe)) {
			DollyApi.getPersonFraPdl(ident.ident || ident, pdlMiljoe)
				.then((response: PdlDataWrapper) => {
					if (!pdlMiljoe) setPdlData(response.data?.data)
					if (pdlMiljoe) setPdlDataQ1(response?.data?.data)
					setPdlLoading(false)
					const feil = response.data?.errors?.find((e) => e.path?.some((i) => i === 'hentPerson'))
					if (feil) {
						setPdlError(feil.message)
					}
				})
				.catch(() => {
					setPdlLoading(false)
					setPdlError('Henting av data feilet')
				})
		}
		if (pdlError) {
			return (
				<div className="flexbox--align-center">
					<Icon size={20} kind="report-problem-circle" />
					<div>
						<pre className="api-feilmelding" style={{ fontSize: '1.25em', marginLeft: '5px' }}>
							{pdlError}
						</pre>
					</div>
				</div>
			)
		}
		return <PdlVisning pdlData={pdlMiljoe ? pdlDataQ1 : pdlData} loading={pdlLoading} />
	}

	if (!ident) {
		return null
	}

	return (
		<div className="flexbox--flex-wrap">
			<Tooltip
				overlay={getPersonInfo}
				placement="top"
				align={{
					offset: [0, -10],
				}}
				mouseEnterDelay={0.1}
				mouseLeaveDelay={0.1}
				arrowContent={<div className="rc-tooltip-arrow-inner" />}
				overlayStyle={{ opacity: 1 }}
				destroyTooltipOnHide={{ keepParent: false }}
			>
				<div className="miljoe-knapp">PDL</div>
			</Tooltip>
			<Tooltip
				overlay={() => getPersonInfo('Q1')}
				placement="top"
				align={{
					offset: [0, -10],
				}}
				mouseEnterDelay={0.1}
				mouseLeaveDelay={0.1}
				arrowContent={<div className="rc-tooltip-arrow-inner" />}
				overlayStyle={{ opacity: 1 }}
				destroyTooltipOnHide={{ keepParent: false }}
			>
				<div className="miljoe-knapp">Q1</div>
			</Tooltip>
		</div>
	)
}
