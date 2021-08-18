import React from 'react'

import SyntaxHighlighter from 'react-syntax-highlighter'
import styled from 'styled-components'

const StyledSyntaxHighlighter = styled(SyntaxHighlighter)`
	font-size: 0.9em;
	max-width: 820px;
`

type Props = {
	code: string
	language: string
}

export default ({ code, language }: Props) => (
	<StyledSyntaxHighlighter language={language}>{code}</StyledSyntaxHighlighter>
)
